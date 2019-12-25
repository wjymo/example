package com.wjy.xunwu.service.house;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.wjy.xunwu.constants.HouseStatus;
import com.wjy.xunwu.dao.*;
import com.wjy.xunwu.dto.HouseDTO;
import com.wjy.xunwu.dto.HouseDetailDTO;
import com.wjy.xunwu.dto.HousePictureDTO;
import com.wjy.xunwu.entity.*;
import com.wjy.xunwu.es.HouseIndexMessage;
import com.wjy.xunwu.exception.XunwuException;
import com.wjy.xunwu.form.*;
import com.wjy.xunwu.response.ResultCode;
import com.wjy.xunwu.response.ServiceMultiResult;
import com.wjy.xunwu.response.ServiceResult;
import com.wjy.xunwu.service.search.SearchService;
import com.wjy.xunwu.util.HouseSort;
import com.wjy.xunwu.util.Tool;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class HouseService {
    @Autowired
    private HouseDAO houseDAO;
    @Autowired
    private SubwayDAO subwayDAO;
    @Autowired
    private SubwayStationDAO subwayStationDAO;
    @Autowired
    private HouseDetailDAO houseDetailDAO;
    @Autowired
    private HousePictureDAO housePictureDAO;
    @Autowired
    private HouseTagDAO houseTagDAO;
    @Autowired
    private SubscribeDAO subscribeDAO;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SearchService searchService;

    @Value("${qiniu.cdn}")
    private String cdnPrefix;

    public ServiceMultiResult<HouseDTO> adminQuery(DatatableSearch searchBody) {
//        List<HouseDTO> houseDTOS = new ArrayList<>();
//        Sort sort = new Sort(Sort.Direction.fromString(searchBody.getDirection()), searchBody.getOrderBy());
        int page = searchBody.getStart() / searchBody.getLength()+1;
        Page<House> housePage = PageHelper.startPage(page, searchBody.getLength()
                ,Tool.humpToLine2(searchBody.getOrderBy()).concat(" ").concat(searchBody.getDirection()));
        List<House> houses = houseDAO.findByCondition(searchBody);

//        Pageable pageable = new PageRequest(page, searchBody.getLength(), sort);

//        Specification<House> specification = (root, query, cb) -> {
//            Predicate predicate = cb.equal(root.get("adminId"), LoginUserUtil.getLoginUserId());
//            predicate = cb.and(predicate, cb.notEqual(root.get("status"), HouseStatus.DELETED.getValue()));
//
//            if (searchBody.getCity() != null) {
//                predicate = cb.and(predicate, cb.equal(root.get("cityEnName"), searchBody.getCity()));
//            }
//
//            if (searchBody.getStatus() != null) {
//                predicate = cb.and(predicate, cb.equal(root.get("status"), searchBody.getStatus()));
//            }
//
//            if (searchBody.getCreateTimeMin() != null) {
//                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createTime"), searchBody.getCreateTimeMin()));
//            }
//
//            if (searchBody.getCreateTimeMax() != null) {
//                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("createTime"), searchBody.getCreateTimeMax()));
//            }
//
//            if (searchBody.getTitle() != null) {
//                predicate = cb.and(predicate, cb.like(root.get("title"), "%" + searchBody.getTitle() + "%"));
//            }
//
//            return predicate;
//        };

//        Page<House> houses = houseRepository.findAll(specification, pageable);
//        houses.forEach(house -> {
//            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
//            houseDTO.setCover(this.cdnPrefix + house.getCover());
//            houseDTOS.add(houseDTO);
//        });

//        return new ServiceMultiResult<>(houses.getTotalElements(), houseDTOS);
        List<HouseDTO> houseDTOS = houses.stream().map(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix + house.getCover());
            return houseDTO;
        }).collect(Collectors.toList());
        return new ServiceMultiResult<HouseDTO>(housePage.getTotal(), houseDTOS);
    }


    public HouseDTO save(HouseForm houseForm) {
        HouseDetail detail = new HouseDetail();
        wrapperDetailInfo(detail, houseForm);

        House house = new House();
        modelMapper.map(houseForm, house);

        Date now = new Date();
        house.setCreateTime(now);
        house.setLastUpdateTime(now);
//        house.setAdminId(LoginUserUtil.getLoginUserId());
        house.setAdminId(2l);
        houseDAO.save(house);

        detail.setHouseId(house.getId());
        houseDetailDAO.save(detail);

        List<HousePicture> pictures = generatePictures(houseForm, house.getId());
        housePictureDAO.saveList(pictures);

        HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
        HouseDetailDTO houseDetailDTO = modelMapper.map(detail, HouseDetailDTO.class);

        houseDTO.setHouseDetail(houseDetailDTO);

        List<HousePictureDTO> pictureDTOS = new ArrayList<>();
        pictures.forEach(housePicture -> pictureDTOS.add(modelMapper.map(housePicture, HousePictureDTO.class)));
        houseDTO.setPictures(pictureDTOS);
        houseDTO.setCover(this.cdnPrefix + houseDTO.getCover());

        List<String> tags = houseForm.getTags();
        if (tags != null && !tags.isEmpty()) {
            List<HouseTag> houseTags = new ArrayList<>();
            for (String tag : tags) {
                houseTags.add(new HouseTag(house.getId(), tag));
            }
            houseTagDAO.saveList(houseTags);
            houseDTO.setTags(tags);
        }

        return houseDTO;
    }

    /**
     * 房源详细信息对象填充
     * @param houseDetail
     * @param houseForm
     * @return
     */
    private void wrapperDetailInfo(HouseDetail houseDetail, HouseForm houseForm) {
        Subway subway = subwayDAO.findOne(houseForm.getSubwayLineId());
        if (subway == null) {
            throw  new XunwuException(ResultCode.NOT_VALID_SUBWAY_LINE);
        }

        SubwayStation subwayStation = subwayStationDAO.findOne(houseForm.getSubwayStationId());
        if (subwayStation == null || subway.getId() != subwayStation.getSubwayId()) {
            throw new XunwuException(ResultCode.NOT_VALID_SUBWAY_STATION);
        }

        houseDetail.setSubwayLineId(subway.getId());
        houseDetail.setSubwayLineName(subway.getName());

        houseDetail.setSubwayStationId(subwayStation.getId());
        houseDetail.setSubwayStationName(subwayStation.getName());

        houseDetail.setDescription(houseForm.getDescription());
        houseDetail.setDetailAddress(houseForm.getDetailAddress());
        houseDetail.setLayoutDesc(houseForm.getLayoutDesc());
        houseDetail.setRentWay(houseForm.getRentWay());
        houseDetail.setRoundService(houseForm.getRoundService());
        houseDetail.setTraffic(houseForm.getTraffic());

    }

    /**
     * 图片对象列表信息填充
     * @param form
     * @param houseId
     * @return
     */
    private List<HousePicture> generatePictures(HouseForm form, Long houseId) {
        List<HousePicture> pictures = new ArrayList<>();
        if (form.getPhotos() == null || form.getPhotos().isEmpty()) {
            return pictures;
        }

        for (PhotoForm photoForm : form.getPhotos()) {
            HousePicture picture = new HousePicture();
            picture.setHouseId(houseId);
            picture.setCdnPrefix(cdnPrefix);
            picture.setPath(photoForm.getPath());
            picture.setWidth(photoForm.getWidth());
            picture.setHeight(photoForm.getHeight());
            pictures.add(picture);
        }
        return pictures;
    }


    private static final String INDEX_TOPIC = "xunwu";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = INDEX_TOPIC)
    private void handleMessage(String content) {
        try {
            HouseIndexMessage message = JSON.parseObject(content,HouseIndexMessage.class);
            switch (message.getOperation()) {
                case HouseIndexMessage.INDEX:
//                    createOrUpdateIndex(message);
                    searchService.index(message.getHouseId());
                    break;
                case HouseIndexMessage.REMOVE:
//                    removeIndex(message);
                    searchService.remove(message.getHouseId());
                    break;
                default:
                    log.warn("Not support message content " + content);
                    break;
            }
        } catch (Exception e) {
            log.error("Cannot parse json for " + content, e);
        }
    }

    public ServiceResult updateStatus(Long id, int status) {
        House house = houseDAO.findOne(id);
        if (house == null) {
            return ServiceResult.notFound();
        }

        if (house.getStatus() == status) {
            return new ServiceResult(false, "状态没有发生变化");
        }

        if (house.getStatus() == HouseStatus.RENTED.getValue()) {
            return new ServiceResult(false, "已出租的房源不允许修改状态");
        }

        if (house.getStatus() == HouseStatus.DELETED.getValue()) {
            return new ServiceResult(false, "已删除的资源不允许操作");
        }

        houseDAO.updateStatus(id, status);

        HouseIndexMessage houseIndexMessage=new HouseIndexMessage(id);
        // 上架更新索引 其他情况都要删除索引
        if (status == HouseStatus.PASSES.getValue()) {
//            searchService.index(id);
            houseIndexMessage.setOperation(HouseIndexMessage.INDEX);
            kafkaTemplate.send(INDEX_TOPIC, JSON.toJSONString(houseIndexMessage));
        } else {
//            searchService.remove(id);
            houseIndexMessage.setOperation(HouseIndexMessage.REMOVE);
            kafkaTemplate.send(INDEX_TOPIC, JSON.toJSONString(houseIndexMessage));
        }
        return ServiceResult.success();
    }

    public ServiceResult<HouseDTO> findCompleteOne(Long id) {
        House house = houseDAO.findOne(id);
        if (house == null) {
            return ServiceResult.notFound();
        }

        HouseDetail detail = houseDetailDAO.findByHouseId(id);
        List<HousePicture> pictures = housePictureDAO.findAllByHouseId(id);

        HouseDetailDTO detailDTO = modelMapper.map(detail, HouseDetailDTO.class);
        List<HousePictureDTO> pictureDTOS = new ArrayList<>();
        for (HousePicture picture : pictures) {
            HousePictureDTO pictureDTO = modelMapper.map(picture, HousePictureDTO.class);
            pictureDTOS.add(pictureDTO);
        }


        List<HouseTag> tags = houseTagDAO.findAllByHouseId(id);
        List<String> tagList = new ArrayList<>();
        for (HouseTag tag : tags) {
            tagList.add(tag.getName());
        }

        HouseDTO result = modelMapper.map(house, HouseDTO.class);
        result.setHouseDetail(detailDTO);
        result.setPictures(pictureDTOS);
        result.setTags(tagList);

//        if (LoginUserUtil.getLoginUserId() > 0) { // 已登录用户
//            HouseSubscribe subscribe = subscribeDAO.findByHouseIdAndUserId(house.getId(), LoginUserUtil.getLoginUserId());
            HouseSubscribe subscribe = subscribeDAO.findByHouseIdAndUserId(house.getId(), 9l);
            if (subscribe != null) {
                result.setSubscribeStatus(subscribe.getStatus());
            }
//        }

        return ServiceResult.of(result);
    }

    public ServiceResult update(HouseForm houseForm) {
        House house = this.houseDAO.findOne(houseForm.getId());
        if (house == null) {
            return ServiceResult.notFound();
        }

        HouseDetail detail = this.houseDetailDAO.findByHouseId(house.getId());
        if (detail == null) {
            return ServiceResult.notFound();
        }

        wrapperDetailInfo(detail, houseForm);
        houseDetailDAO.save(detail);

        List<HousePicture> pictures = generatePictures(houseForm, houseForm.getId());
        housePictureDAO.saveList(pictures);

        if (houseForm.getCover() == null) {
            houseForm.setCover(house.getCover());
        }

        modelMapper.map(houseForm, house);
        house.setLastUpdateTime(new Date());
        houseDAO.save(house);

        if (house.getStatus() == HouseStatus.PASSES.getValue()) {
            searchService.index(house.getId());
        }

        return ServiceResult.success();
    }

    public ServiceResult addTag(Long houseId, String tag) {
        House house = houseDAO.findOne(houseId);
        if (house == null) {
            return ServiceResult.notFound();
        }

        HouseTag houseTag = houseTagDAO.findByNameAndHouseId(tag, houseId);
        if (houseTag != null) {
            return new ServiceResult(false, "标签已存在");
        }

        houseTagDAO.save(new HouseTag(houseId, tag));
        return ServiceResult.success();
    }


    public ServiceMultiResult<HouseDTO> query(RentSearch rentSearch) {
        String keywords = rentSearch.getKeywords();
        if (StringUtils.isNotEmpty(keywords)) {
            ServiceMultiResult<Long> serviceResult = searchService.query(rentSearch);
            if (serviceResult.getTotal() == 0) {
                return new ServiceMultiResult<>(0, new ArrayList<>());
            }
            return new ServiceMultiResult<>(serviceResult.getTotal(), wrapperHouseResult(serviceResult.getResult()));
        }

        return simpleQuery(rentSearch);

    }

    private List<HouseDTO> wrapperHouseResult(List<Long> houseIds) {
        List<HouseDTO> result = new ArrayList<>();

        Map<Long, HouseDTO> idToHouseMap = new HashMap<>();
        List<House> houses = houseDAO.findAllInIds(houseIds);
        houses.forEach(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix + house.getCover());
            idToHouseMap.put(house.getId(), houseDTO);
        });

        wrapperHouseList(houseIds, idToHouseMap);

        // 矫正顺序
        for (Long houseId : houseIds) {
            result.add(idToHouseMap.get(houseId));
        }
        return result;
    }


    private ServiceMultiResult<HouseDTO> simpleQuery(RentSearch rentSearch) {
//        Sort sort = HouseSort.generateSort(rentSearch.getOrderBy(), rentSearch.getOrderDirection());
        String sort = HouseSort.generateSort(rentSearch.getOrderBy(), rentSearch.getOrderDirection());
        int page = rentSearch.getStart() / rentSearch.getSize()+1;

//        Pageable pageable = new PageRequest(page, rentSearch.getSize(), sort);

//        Specification<House> specification = (root, criteriaQuery, criteriaBuilder) -> {
//            Predicate predicate = criteriaBuilder.equal(root.get("status"), HouseStatus.PASSES.getValue());
//
//            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("cityEnName"), rentSearch.getCityEnName()));
//
//            if (HouseSort.DISTANCE_TO_SUBWAY_KEY.equals(rentSearch.getOrderBy())) {
//                predicate = criteriaBuilder.and(predicate, criteriaBuilder.gt(root.get(HouseSort.DISTANCE_TO_SUBWAY_KEY), -1));
//            }
//            return predicate;
//        };

//        Page<House> houses = houseRepository.findAll(specification, pageable);

        Page<House> houses = PageHelper.startPage(page, rentSearch.getSize(), sort);
        List<House> houseList= houseDAO.findByConditionForApi(rentSearch);


        List<HouseDTO> houseDTOS = new ArrayList<>();


        List<Long> houseIds = new ArrayList<>();
        Map<Long, HouseDTO> idToHouseMap = Maps.newHashMap();
        houseList.forEach(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix + house.getCover());
            houseDTOS.add(houseDTO);

            houseIds.add(house.getId());
            idToHouseMap.put(house.getId(), houseDTO);
        });


        wrapperHouseList(houseIds, idToHouseMap);
        return new ServiceMultiResult<HouseDTO>(houses.getTotal(), houseDTOS);
    }

    /**
     * 渲染详细信息 及 标签
     * @param houseIds
     * @param idToHouseMap
     */
    private void wrapperHouseList(List<Long> houseIds, Map<Long, HouseDTO> idToHouseMap) {
        List<HouseDetail> details = houseDetailDAO.findAllByHouseIdIn(houseIds);
        details.forEach(houseDetail -> {
            HouseDTO houseDTO = idToHouseMap.get(houseDetail.getHouseId());
            HouseDetailDTO detailDTO = modelMapper.map(houseDetail, HouseDetailDTO.class);
            houseDTO.setHouseDetail(detailDTO);
        });

        List<HouseTag> houseTags = houseTagDAO.findAllByHouseIdIn(houseIds);
        houseTags.forEach(houseTag -> {
            HouseDTO house = idToHouseMap.get(houseTag.getHouseId());
            house.getTags().add(houseTag.getName());
        });
    }

    public ServiceMultiResult<HouseDTO> wholeMapQuery(MapSearch mapSearch) {
        ServiceMultiResult<Long> serviceResult = searchService.mapQuery(mapSearch.getCityEnName(),
                mapSearch.getOrderBy(), mapSearch.getOrderDirection(), mapSearch.getStart(), mapSearch.getSize());

        if (serviceResult.getTotal() == 0) {
            return new ServiceMultiResult<>(0, new ArrayList<>());
        }
        List<HouseDTO> houses = wrapperHouseResult(serviceResult.getResult());
        return new ServiceMultiResult<>(serviceResult.getTotal(), houses);
    }

    public ServiceMultiResult<HouseDTO> boundMapQuery(MapSearch mapSearch) {
        ServiceMultiResult<Long> serviceResult = searchService.mapQuery(mapSearch);
        if (serviceResult.getTotal() == 0) {
            return new ServiceMultiResult<>(0, new ArrayList<>());
        }

        List<HouseDTO> houses = wrapperHouseResult(serviceResult.getResult());
        return new ServiceMultiResult<>(serviceResult.getTotal(), houses);
    }
}
