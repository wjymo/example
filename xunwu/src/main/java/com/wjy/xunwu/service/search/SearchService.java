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
import com.wjy.xunwu.dto.HouseDTO;
import com.wjy.xunwu.entity.House;
import com.wjy.xunwu.entity.HouseDetail;
import com.wjy.xunwu.entity.HouseTag;
import com.wjy.xunwu.es.HouseIndexTemplate;
import com.wjy.xunwu.es.HouseSuggest;
import com.wjy.xunwu.form.Interval;
import com.wjy.xunwu.form.RentSearch;
import com.wjy.xunwu.response.ServiceMultiResult;
import com.wjy.xunwu.response.ServiceResult;
import com.wjy.xunwu.util.HouseSort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.frameworkset.elasticsearch.ElasticSearchHelper;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.client.ClientOptions;
import org.frameworkset.elasticsearch.entity.ESDatas;
import org.frameworkset.elasticsearch.entity.RestResponse;
import org.frameworkset.elasticsearch.entity.SearchHit;
import org.frameworkset.elasticsearch.entity.SearchHits;
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

//    public ServiceMultiResult<Long> query(RentSearch rentSearch) {
//        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//
//        boolQuery.filter(
//                QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, rentSearch.getCityEnName())
//        );
//
//        if (rentSearch.getRegionEnName() != null && !"*".equals(rentSearch.getRegionEnName())) {
//            boolQuery.filter(
//                    QueryBuilders.termQuery(HouseIndexKey.REGION_EN_NAME, rentSearch.getRegionEnName())
//            );
//        }
//
//        RentValueBlock area = RentValueBlock.matchArea(rentSearch.getAreaBlock());
//        if (!RentValueBlock.ALL.equals(area)) {
//            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(HouseIndexKey.AREA);
//            if (area.getMax() > 0) {
//                rangeQueryBuilder.lte(area.getMax());
//            }
//            if (area.getMin() > 0) {
//                rangeQueryBuilder.gte(area.getMin());
//            }
//            boolQuery.filter(rangeQueryBuilder);
//        }
//
//        RentValueBlock price = RentValueBlock.matchPrice(rentSearch.getPriceBlock());
//        if (!RentValueBlock.ALL.equals(price)) {
//            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(HouseIndexKey.PRICE);
//            if (price.getMax() > 0) {
//                rangeQuery.lte(price.getMax());
//            }
//            if (price.getMin() > 0) {
//                rangeQuery.gte(price.getMin());
//            }
//            boolQuery.filter(rangeQuery);
//        }
//
//        if (rentSearch.getDirection() > 0) {
//            boolQuery.filter(
//                    QueryBuilders.termQuery(HouseIndexKey.DIRECTION, rentSearch.getDirection())
//            );
//        }
//
//        if (rentSearch.getRentWay() > -1) {
//            boolQuery.filter(
//                    QueryBuilders.termQuery(HouseIndexKey.RENT_WAY, rentSearch.getRentWay())
//            );
//        }
//
////        boolQuery.must(
////                QueryBuilders.matchQuery(HouseIndexKey.TITLE, rentSearch.getKeywords())
////                        .boost(2.0f)
////        );
//
//        boolQuery.must(
//                QueryBuilders.multiMatchQuery(rentSearch.getKeywords(),
//                        HouseIndexKey.TITLE,
//                        HouseIndexKey.TRAFFIC,
//                        HouseIndexKey.DISTRICT,
//                        HouseIndexKey.ROUND_SERVICE,
//                        HouseIndexKey.SUBWAY_LINE_NAME,
//                        HouseIndexKey.SUBWAY_STATION_NAME
//                ));
//
//        SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME)
//                .setTypes(INDEX_TYPE)
//                .setQuery(boolQuery)
//                .addSort(
//                        HouseSort.getSortKey(rentSearch.getOrderBy()),
//                        SortOrder.fromString(rentSearch.getOrderDirection())
//                )
//                .setFrom(rentSearch.getStart())
//                .setSize(rentSearch.getSize())
//                .setFetchSource(HouseIndexKey.HOUSE_ID, null);
//
//        log.debug(requestBuilder.toString());
//
//        List<Long> houseIds = new ArrayList<>();
//        SearchResponse response = requestBuilder.get();
//        if (response.status() != RestStatus.OK) {
//            logger.warn("Search status is no ok for " + requestBuilder);
//            return new ServiceMultiResult<>(0, houseIds);
//        }
//
//        for (SearchHit hit : response.getHits()) {
//            System.out.println(hit.getSource());
//            houseIds.add(Longs.tryParse(String.valueOf(hit.getSource().get(HouseIndexKey.HOUSE_ID))));
//        }
//
//        return new ServiceMultiResult<>(response.getHits().totalHits, houseIds);
//    }


    public void index(Long houseId) {
        House house = houseDAO.findOne(houseId);
        HouseIndexTemplate houseIndexTemplate = new HouseIndexTemplate();
        modelMapper.map(house, houseIndexTemplate);
        HouseDetail houseDetail = houseDetailDAO.findByHouseId(houseId);
        modelMapper.map(houseDetail, houseIndexTemplate);
        List<HouseTag> houseTags = houseTagDAO.findAllByHouseId(houseId);
        List<String> tags = houseTags.stream().map(houseTag -> houseTag.getName()).collect(Collectors.toList());
        houseIndexTemplate.setTags(tags);

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
            return !StringUtils.equals(type, "ARABIC") || StringUtils.length(term) > 1;
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
//        CompletionSuggestionBuilder suggestion = SuggestBuilders.completionSuggestion("suggest").prefix(prefix).size(5);
//
//        SuggestBuilder suggestBuilder = new SuggestBuilder();
//        suggestBuilder.addSuggestion("autocomplete", suggestion);
//
//        SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME)
//                .setTypes(INDEX_TYPE)
//                .suggest(suggestBuilder);
//        logger.debug(requestBuilder.toString());
//
//        SearchResponse response = requestBuilder.get();
//        Suggest suggest = response.getSuggest();
//        if (suggest == null) {
//            return ServiceResult.of(new ArrayList<>());
//        }
//        Suggest.Suggestion result = suggest.getSuggestion("autocomplete");
//
//        int maxSuggest = 0;
//        Set<String> suggestSet = new HashSet<>();
//
//        for (Object term : result.getEntries()) {
//            if (term instanceof CompletionSuggestion.Entry) {
//                CompletionSuggestion.Entry item = (CompletionSuggestion.Entry) term;
//
//                if (item.getOptions().isEmpty()) {
//                    continue;
//                }
//
//                for (CompletionSuggestion.Entry.Option option : item.getOptions()) {
//                    String tip = option.getText().string();
//                    if (suggestSet.contains(tip)) {
//                        continue;
//                    }
//                    suggestSet.add(tip);
//                    maxSuggest++;
//                }
//            }
//
//            if (maxSuggest > 5) {
//                break;
//            }
//        }
//        List<String> suggests = Lists.newArrayList(suggestSet.toArray(new String[]{}));
//        return ServiceResult.of(suggests);
        return null;
    }
}
