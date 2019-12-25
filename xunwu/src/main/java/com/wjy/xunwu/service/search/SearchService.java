package com.wjy.xunwu.service.search;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;
import com.wjy.xunwu.constants.HouseIndexKey;
import com.wjy.xunwu.constants.RentValueBlock;
import com.wjy.xunwu.dao.HouseDAO;
import com.wjy.xunwu.dao.HouseDetailDAO;
import com.wjy.xunwu.dao.HouseTagDAO;
import com.wjy.xunwu.dao.SupportAddressDAO;
import com.wjy.xunwu.dto.HouseDTO;
import com.wjy.xunwu.entity.House;
import com.wjy.xunwu.entity.HouseDetail;
import com.wjy.xunwu.entity.HouseTag;
import com.wjy.xunwu.entity.SupportAddress;
import com.wjy.xunwu.es.BaiduMapLocation;
import com.wjy.xunwu.es.HouseBucketDTO;
import com.wjy.xunwu.es.HouseIndexTemplate;
import com.wjy.xunwu.es.HouseSuggest;
import com.wjy.xunwu.form.Interval;
import com.wjy.xunwu.form.MapSearch;
import com.wjy.xunwu.form.RentSearch;
import com.wjy.xunwu.response.ServiceMultiResult;
import com.wjy.xunwu.response.ServiceResult;
import com.wjy.xunwu.service.house.AddressService;
import com.wjy.xunwu.util.HouseSort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.frameworkset.elasticsearch.ElasticSearchHelper;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.client.ClientOptions;
import org.frameworkset.elasticsearch.entity.*;
import org.frameworkset.elasticsearch.entity.suggest.CompleteOption;
import org.frameworkset.elasticsearch.entity.suggest.CompleteRestResponse;
import org.frameworkset.elasticsearch.entity.suggest.CompleteSuggest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchService {
    @Autowired
    private HouseDAO houseDAO;
    @Autowired
    private HouseDetailDAO houseDetailDAO;
    @Autowired
    private HouseTagDAO houseTagDAO;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SupportAddressDAO supportAddressDAO;
    @Autowired
    private AddressService addressService;



    public void index(Long houseId) {
        House house = houseDAO.findOne(houseId);
        HouseIndexTemplate houseIndexTemplate = new HouseIndexTemplate();
        modelMapper.map(house, houseIndexTemplate);
        HouseDetail houseDetail = houseDetailDAO.findByHouseId(houseId);
        modelMapper.map(houseDetail, houseIndexTemplate);
        List<HouseTag> houseTags = houseTagDAO.findAllByHouseId(houseId);
        List<String> tags = houseTags.stream().map(houseTag -> houseTag.getName()).collect(Collectors.toList());
        houseIndexTemplate.setTags(tags);

        SupportAddress city = supportAddressDAO.findByEnNameAndLevel(house.getCityEnName(), SupportAddress.Level.CITY.getValue());
        SupportAddress region = supportAddressDAO.findByEnNameAndLevel(house.getRegionEnName(), SupportAddress.Level.REGION.getValue());
        String address = city.getCnName() + region.getCnName() + house.getStreet() + house.getDistrict()
                + houseDetail.getDetailAddress();
        ServiceResult<BaiduMapLocation> location = addressService.getBaiduMapLocation(city.getCnName(), address);
//        if (!location.isSuccess()) {
//            this.index(message.getHouseId(), message.getRetry() + 1);
//            return;
//        }
        houseIndexTemplate.setLocation(location.getResult());


        ClientInterface clientUtil = ElasticSearchHelper.getConfigRestClientUtil("esmappers/xunwu.xml");
        ImmutableMap<Object, Object> map = ImmutableMap.builder().put(HouseIndexKey.HOUSE_ID, houseId).build();
        RestResponse restResponse = clientUtil
                .search("xunwu/_search", "getById", map, HouseIndexTemplate.class);
        SearchHits searchHits = restResponse.getSearchHits();
        Integer totalHit = (Integer) searchHits.getTotal();
        List<SearchHit> hits = searchHits.getHits();
        if (totalHit == 0) {
            create(houseIndexTemplate);
        } else if (totalHit == 1) {
            String esId = searchHits.getHits().get(0).getId();
            update(esId, houseIndexTemplate);
        } else {
            deleteAndCreate(totalHit, houseIndexTemplate);
        }
        List<HouseIndexTemplate> collect =
                hits.stream().map(searchHit -> (HouseIndexTemplate) searchHit.getSource()).collect(Collectors.toList());
        System.out.println(1);
    }

    public void create(HouseIndexTemplate indexTemplate) {
//        if (!updateSuggest(indexTemplate)) {
//            return false;
//        }
        updateSuggest(indexTemplate);
        ClientInterface clientUtil = ElasticSearchHelper.getRestClientUtil();
        ClientOptions clientOptions = new ClientOptions();
        clientOptions.setIdField("houseId");
        String s = clientUtil.addDocument("xunwu", "_doc", indexTemplate, clientOptions);

//        try {
//            IndexResponse response = this.esClient.prepareIndex(INDEX_NAME, INDEX_TYPE)
//                    .setSource(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON).get();
//
//            log.debug("Create index with house: " + indexTemplate.getHouseId());
//            if (response.status() == RestStatus.CREATED) {
//                return true;
//            } else {
//                return false;
//            }
//        } catch (JsonProcessingException e) {
//            logger.error("Error to index house " + indexTemplate.getHouseId(), e);
//            return false;
//        }
    }

    public void update(String esId, HouseIndexTemplate indexTemplate) {
//        if (!updateSuggest(indexTemplate)) {
//            return false;
//        }
        updateSuggest(indexTemplate);
        ClientInterface clientUtil = ElasticSearchHelper.getRestClientUtil();
        String s = clientUtil.updateDocument("xunwu", "_doc", esId, indexTemplate);
//        try {
//            UpdateResponse response = this.esClient.prepareUpdate
// (INDEX_NAME, INDEX_TYPE, esId).setDoc(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON).get();
//
//            logger.debug("Update index with house: " + indexTemplate.getHouseId());
//            if (response.status() == RestStatus.OK) {
//                return true;
//            } else {
//                return false;
//            }
//        } catch (JsonProcessingException e) {
//            logger.error("Error to index house " + indexTemplate.getHouseId(), e);
//            return false;
//        }
    }

    public void deleteAndCreate(long totalHit, HouseIndexTemplate indexTemplate) {
        ClientInterface clientUtil = ElasticSearchHelper.getRestClientUtil();
        String s = clientUtil.deleteDocument("xunwu", "_doc", indexTemplate.getHouseId().toString());
        System.out.println(1);
        create(indexTemplate);

//        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
//                .newRequestBuilder(esClient)
//                .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, indexTemplate.getHouseId()))
//                .source(INDEX_NAME);
//
//        logger.debug("Delete by query for house: " + builder);
//
//        BulkByScrollResponse response = builder.get();
//        long deleted = response.getDeleted();
//        if (deleted != totalHit) {
//            logger.warn("Need delete {}, but {} was deleted!", totalHit, deleted);
//            return false;
//        } else {
//            return create(indexTemplate);
//        }
    }


    public void remove(Long houseId) {
        ClientInterface clientUtil = ElasticSearchHelper.getRestClientUtil();
        String s = clientUtil.deleteDocument("xunwu", "_doc", houseId.toString());
        log.info(s);
    }


    public ServiceMultiResult<Long> query(RentSearch rentSearch) {
        RentValueBlock area = RentValueBlock.matchArea(rentSearch.getAreaBlock());
        if (!RentValueBlock.ALL.equals(area)) {
            int areaMax = area.getMax();
            int areaMin = area.getMin();
            Interval areaInterval = new Interval().setMax(areaMax).setMin(areaMin);
            rentSearch.setAreaInterval(areaInterval);
//            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(HouseIndexKey.AREA);
//            if (area.getMax() > 0) {
//                rangeQueryBuilder.lte(area.getMax());
//            }
//            if (area.getMin() > 0) {
//                rangeQueryBuilder.gte(area.getMin());
//            }
//            boolQuery.filter(rangeQueryBuilder);
        }
        RentValueBlock price = RentValueBlock.matchPrice(rentSearch.getPriceBlock());
        if (!RentValueBlock.ALL.equals(price)) {
            int priceMax = price.getMax();
            int priceMin = price.getMin();
            Interval priceInterval = new Interval().setMax(priceMax).setMin(priceMin);
            rentSearch.setPriceInterval(priceInterval);
        }


        ClientInterface clientUtil = ElasticSearchHelper.getConfigRestClientUtil("esmappers/xunwu.xml");
        ESDatas<Map> houseDTOESDatas = clientUtil.searchList("xunwu/_search", "getByCondition", rentSearch, Map.class);
        long totalSize = houseDTOESDatas.getTotalSize();
        List<Map> datas = houseDTOESDatas.getDatas();
        if (!CollectionUtils.isEmpty(datas)) {
            List<Long> houseIds = datas.stream()
                    .map(map -> Long.valueOf(String.valueOf(map.get("houseId")))).collect(Collectors.toList());
            return new ServiceMultiResult<>(totalSize, houseIds);
        }
        return new ServiceMultiResult<>(0, null);
    }

    private boolean updateSuggest(HouseIndexTemplate indexTemplate) {
        ClientInterface clientUtil = ElasticSearchHelper.getRestClientUtil();
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("analyzer", "ik_max_word");
        jsonBody.put("text", Arrays.asList(indexTemplate.getTitle(),
                indexTemplate.getLayoutDesc(), indexTemplate.getRoundService(),
                indexTemplate.getDescription(), indexTemplate.getSubwayLineName(),
                indexTemplate.getSubwayStationName()));
        String body = jsonBody.toString();
        String s = clientUtil.executeHttp("_analyze", body, ClientInterface.HTTP_POST);

        JSONObject jsonObject = JSON.parseObject(s);
        JSONArray tokens = jsonObject.getJSONArray("tokens");

        List<HouseSuggest> houseSuggests = tokens.stream().filter(o -> {
            JSONObject token = (JSONObject) o;
            String type = token.getString("type");
            String term = token.getString("token");
            return !StringUtils.equals(type, "ARABIC") && StringUtils.length(term) > 1;
        }).map(o -> {
            JSONObject token = (JSONObject) o;
            String term = token.getString("token");
            HouseSuggest suggest = new HouseSuggest();
            suggest.setInput(term);
            return suggest;
        }).collect(Collectors.toList());


//        AnalyzeRequestBuilder requestBuilder = new AnalyzeRequestBuilder(
//                this.esClient, AnalyzeAction.INSTANCE, INDEX_NAME, indexTemplate.getTitle(),
//                indexTemplate.getLayoutDesc(), indexTemplate.getRoundService(),
//                indexTemplate.getDescription(), indexTemplate.getSubwayLineName(),
//                indexTemplate.getSubwayStationName());
//
//        requestBuilder.setAnalyzer("ik_smart");
//
//        AnalyzeResponse response = requestBuilder.get();
//        List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
//        if (tokens == null) {
//            logger.warn("Can not analyze token for house: " + indexTemplate.getHouseId());
//            return false;
//        }
//
//        List<HouseSuggest> suggests = new ArrayList<>();
//        for (AnalyzeResponse.AnalyzeToken token : tokens) {
//            // 排序数字类型 & 小于2个字符的分词结果
//            if ("<NUM>".equals(token.getType()) || token.getTerm().length() < 2) {
//                continue;
//            }
//
//            HouseSuggest suggest = new HouseSuggest();
//            suggest.setInput(token.getTerm());
//            suggests.add(suggest);
//        }


        // 定制化小区自动补全
        HouseSuggest suggest = new HouseSuggest();
        suggest.setInput(indexTemplate.getDistrict());
        houseSuggests.add(suggest);

        indexTemplate.setSuggest(houseSuggests);
        return true;
    }

    public ServiceResult<List<String>> suggest(String prefix) {
        String suggestKey = "my-suggest";
        Map<String, String> map = ImmutableMap.<String, String>builder().put("suggest-key", suggestKey).put("prefix", prefix).build();
        ClientInterface clientUtil = ElasticSearchHelper.getConfigRestClientUtil("esmappers/xunwu.xml");
        CompleteRestResponse completeRestResponse = clientUtil.complateSuggest("xunwu/_search",
                "completionByPrefix", map);
        Map<String, List<CompleteSuggest>> suggestsMap = completeRestResponse.getSuggests();
        List<CompleteSuggest> completeSuggests = suggestsMap.get(suggestKey);
        Set<String> words = new HashSet<>();
        completeSuggests.forEach(completeSuggest -> {
            List<CompleteOption> options = completeSuggest.getOptions();
            options.stream().forEach(completeOption -> words.add(completeOption.getText()));
        });
        List<String> suggests = Lists.newArrayList(words.toArray(new String[]{}));
        return ServiceResult.of(suggests);
    }

    public ServiceMultiResult<HouseBucketDTO> mapAggregate(String cityEnName) {
        Map<String, String> map = ImmutableMap.<String, String>builder().put("cityEnName", cityEnName)
                .put("aggField", HouseIndexKey.REGION_EN_NAME).build();
        ClientInterface clientUtil = ElasticSearchHelper.getConfigRestClientUtil("esmappers/xunwu.xml");
        Map params = new HashMap();
        ESAggDatas<LongAggHit> aggHitESAggDatas = clientUtil.searchAgg("xunwu/_search", "aggByField", map, LongAggHit.class,
                "aggByFieldKey");
        long totalSize = aggHitESAggDatas.getTotalSize();
        List<LongAggHit> aggDatas = aggHitESAggDatas.getAggDatas();
        List<HouseBucketDTO> buckets = aggDatas.stream().map(longAggHit -> new HouseBucketDTO(longAggHit.getKey().toString(), longAggHit.getDocCount()))
                .collect(Collectors.toList());
//        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//        boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, cityEnName));
//
//        AggregationBuilder aggBuilder = AggregationBuilders.terms(HouseIndexKey.AGG_REGION)
//                .field(HouseIndexKey.REGION_EN_NAME);
//        SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME)
//                .setTypes(INDEX_TYPE)
//                .setQuery(boolQuery)
//                .addAggregation(aggBuilder);
//
//        logger.debug(requestBuilder.toString());
//
//        SearchResponse response = requestBuilder.get();
//        List<HouseBucketDTO> buckets = new ArrayList<>();
//        if (response.status() != RestStatus.OK) {
//            logger.warn("Aggregate status is not ok for " + requestBuilder);
//            return new ServiceMultiResult<>(0, buckets);
//        }
//
//        Terms terms = response.getAggregations().get(HouseIndexKey.AGG_REGION);
//        for (Terms.Bucket bucket : terms.getBuckets()) {
//            buckets.add(new HouseBucketDTO(bucket.getKeyAsString(), bucket.getDocCount()));
//        }
//
        return new ServiceMultiResult<>(totalSize, buckets);
    }


    public ServiceMultiResult<Long> mapQuery(String cityEnName, String orderBy,
                                             String orderDirection,
                                             int start,
                                             int size) {
        ImmutableMap<String, Object> map = ImmutableMap.<String, Object>builder().put("orderBy", orderBy)
                .put("direction", orderDirection).put("cityEnName",cityEnName)
                .put("from",start).put("size",size).build();
        ClientInterface clientUtil = ElasticSearchHelper.getConfigRestClientUtil("esmappers/xunwu.xml");
        ESDatas<Map> esDatas = clientUtil.searchList("xunwu/_search", "sortBy", map, Map.class);
        List<Map> datas = esDatas.getDatas();
        List<Long> houseIds = datas.stream().map(mapData ->
                Longs.tryParse(String.valueOf(mapData.get(HouseIndexKey.HOUSE_ID))))
                .collect(Collectors.toList());
        long totalSize = esDatas.getTotalSize();
        return new ServiceMultiResult<>(totalSize, houseIds);
    }


    public ServiceMultiResult<Long> mapQuery(MapSearch mapSearch) {
        ImmutableMap<String, Object> map = ImmutableMap.<String, Object>builder().put("orderBy", mapSearch.getOrderBy())
                .put("direction", mapSearch.getOrderDirection()).put("cityEnName",mapSearch.getCityEnName())
                .put("from",mapSearch.getStart()).put("size",mapSearch.getSize())
                .put("leftLatitude",mapSearch.getLeftLatitude()).put("leftLongitude",mapSearch.getLeftLongitude())
                .put("rightLatitude",mapSearch.getRightLatitude()).put("rightLongitude",mapSearch.getRightLongitude())
                .build();
        ClientInterface clientUtil = ElasticSearchHelper.getConfigRestClientUtil("esmappers/xunwu.xml");
        ESDatas<Map> esDatas = clientUtil.searchList("xunwu/_search", "getBylocation", map, Map.class);
        List<Map> datas = esDatas.getDatas();
        if(datas!=null){
            List<Long> houseIds = datas.stream().map(mapData ->
                    Longs.tryParse(String.valueOf(mapData.get(HouseIndexKey.HOUSE_ID))))
                    .collect(Collectors.toList());
            long totalSize = esDatas.getTotalSize();
            return new ServiceMultiResult<>(totalSize, houseIds);
        }
        return new ServiceMultiResult<>(0,null);

//        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//        boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, mapSearch.getCityEnName()));
//
//        boolQuery.filter(
//                QueryBuilders.geoBoundingBoxQuery("location")
//                        .setCorners(
//                                new GeoPoint(mapSearch.getLeftLatitude(), mapSearch.getLeftLongitude()),
//                                new GeoPoint(mapSearch.getRightLatitude(), mapSearch.getRightLongitude())
//                        ));
//
//        SearchRequestBuilder builder = this.esClient.prepareSearch(INDEX_NAME)
//                .setTypes(INDEX_TYPE)
//                .setQuery(boolQuery)
//                .addSort(HouseSort.getSortKey(mapSearch.getOrderBy()),
//                        SortOrder.fromString(mapSearch.getOrderDirection()))
//                .setFrom(mapSearch.getStart())
//                .setSize(mapSearch.getSize());
//
//        List<Long> houseIds = new ArrayList<>();
//        SearchResponse response = builder.get();
//        if (RestStatus.OK != response.status()) {
//            logger.warn("Search status is not ok for " + builder);
//            return new ServiceMultiResult<>(0, houseIds);
//        }
//
//        for (SearchHit hit : response.getHits()) {
//            houseIds.add(Longs.tryParse(String.valueOf(hit.getSource().get(HouseIndexKey.HOUSE_ID))));
//        }
//        return new ServiceMultiResult<>(response.getHits().getTotalHits(), houseIds);
    }
}
