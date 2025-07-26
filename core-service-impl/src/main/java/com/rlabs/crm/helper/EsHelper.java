
/**
 * @author Manoj Sharma
 */

package com.rlabs.crm.helper;

import com.rlabs.crm.payload.request.elasticsearch.EsMessageRequest;
import com.rlabs.crm.util.MapperUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.MainResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@DependsOn("initConfigurations")
public class EsHelper {

    @Value("${elastic-search-server.index-name}")
    private String esIndex;

    @Autowired
    private MapperUtil mapperUtil;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public UpdateResponse edit(EsMessageRequest esMessageRequest, String documentId){
        UpdateRequest updateRequest = new UpdateRequest(esIndex, documentId);
        updateRequest.doc(mapperUtil.toJson(esMessageRequest), XContentType.JSON);
        UpdateResponse updateResponse = null;
        try{
            updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            log.info("Document updated in elasticsearch. Document id: {}",updateResponse.getId());
        }catch (Exception e){
            log.error("Exception while updating a document from elasticsearch. ",e);
        }
        return updateResponse;
    }

    public IndexResponse add(EsMessageRequest esMessageRequest){
        IndexResponse indexResponse = null;
        try {
            IndexRequest indexRequest = new IndexRequest(esIndex)
                .source(mapperUtil.toJson(esMessageRequest), XContentType.JSON);
            indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            log.info("Document saved in elasticsearch. Document id: {}",indexResponse.getId());
        }catch (Exception e){
            log.error("Exception while saving data in elasticsearch. ",e);
        }
        return indexResponse;
    }

    public DeleteResponse delete(String documentId){
        DeleteResponse deleteResponse = null;
        try {
            DeleteRequest deleteRequest = new DeleteRequest(esIndex, documentId);
            deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            log.info("Document deleted with response: {}", deleteResponse.getResult());
        } catch (IOException e) {
            log.error("Exception while deleting a document from elasticsearch. ",e);
        }
        return deleteResponse;
    }

    public SearchResponse searchEsWithDbIdAndClassName(String dbId, String className){
        SearchRequest searchRequest = new SearchRequest(esIndex);
        String q = "dbId:"+dbId+" AND sourceType:"+className;
        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(q);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryStringQueryBuilder);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("Exception while searching data from elasticsearch. ",e);
        }
        return searchResponse;
    }

    public SearchResponse search(String q){
        SearchRequest searchRequest = new SearchRequest(esIndex);
        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(q);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryStringQueryBuilder);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("Global search failed. ",e);
        }
        return searchResponse;
    }

    public boolean checkIndexExists() throws IOException{
        try{
            GetIndexRequest request = new GetIndexRequest(esIndex);
            return restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        }catch (IOException e){
            log.error("Exception while checking if index exists or not in elasticsearch. ",e);
            throw e;
        }
    }

    public CreateIndexResponse createIndex() throws IOException {
        CreateIndexResponse createIndexResponse = null;
        try{
            CreateIndexRequest request = new CreateIndexRequest(esIndex);
            createIndexResponse = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
            log.info("Index created: {}", createIndexResponse.index());
        }catch (IOException e){
            log.error("Exception while creating index in elasticsearch. ",e);
            throw e;
        }
        return createIndexResponse;
    }

    @PostConstruct
    public void checkEsAvailabilityAndIndexStatus() {

        try {
            MainResponse response = restHighLevelClient.info(RequestOptions.DEFAULT);
            if(response == null){
                log.error("Elasticsearch is not up. Application exit");
                System.exit(1);
            }
            log.info("Elasticsearch connection available");
        }catch (IOException e) {
            log.error("Exception in checking elasticsearch status. Elasticsearch is not up. Application exit");
            System.exit(1);
        }
        try{
            if (!checkIndexExists()) {
                createIndex();
                log.info("Elasticsearch Index created successfully.");
            } else {
                log.info("Elasticsearch index already exists.");
            }
        } catch (IOException   e) {
            log.error("Exception while checking elasticsearch index status and creating it if not present. Application exit");
            System.exit(1);
        }
    }

}
