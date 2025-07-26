package com.rlabs.crm.controller.globalsearch;

import com.rlabs.crm.api.controller.GlobalSearchApi;
import com.rlabs.crm.helper.EsHelper;
import com.rlabs.crm.util.MapperUtil;
import com.rlabs.crm.web.rest.errors.globalsearch.GlobalSearchFailedException;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class GlobalSearchController implements GlobalSearchApi {

    @Autowired
    private MapperUtil mapperUtil;

    @Autowired
    private EsHelper esHelper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public ResponseEntity<Object> globalSearch(String q) {
        log.info("Request to search from elastic-search. Text to search: {}",q);
        SearchResponse searchResponse = esHelper.search(q);
        if(searchResponse == null){
            throw new GlobalSearchFailedException();
        }
        // Process the search results
        List<LinkedHashMap> list = new ArrayList<>();
        searchResponse.getHits().forEach(hit -> {
            LinkedHashMap obj = mapperUtil.toObject(hit.getSourceAsString(), LinkedHashMap.class);
            list.add(obj);
        });
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

}
