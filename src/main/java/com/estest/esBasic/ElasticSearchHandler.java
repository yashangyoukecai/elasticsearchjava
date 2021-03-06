package com.estest.esBasic;

import com.estest.bean.MapContent;
import com.estest.bean.Medicine;
import com.google.common.collect.Maps;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wklmogujie on 16/1/4.
 */
public class ElasticSearchHandler {

    private Client client;

    public ElasticSearchHandler() {
        //使用本机做为节点
        this("127.0.0.1");
    }

    public ElasticSearchHandler(String ipAddress) {
        String clusterName = "elasticsearch";
        Settings settings = Settings.settingsBuilder()
                .put("cluster.name", clusterName)
                .put("client.transport.ping_timeout", "10s")
//                .put("shield.user", "tribe_user:tribe_user")
                .build();
        try {
            client = TransportClient.builder()
//                    .addPlugin(ShieldPlugin.class)
                    .settings(settings).build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ipAddress), 9300));
//            String token = basicAuthHeaderValue("tribe_user",
//                    new SecuredString("tribe_user".toCharArray()));
//
//            client.prepareSearch().putHeader("Authorization", token).get();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void closeESClient() {
        client.close();
    }

    /**
     * 建立索引,索引建立好之后,会在elasticsearch-0.20.6\data\elasticsearch\nodes\0创建所以你看
     *
     * @param indexname 为索引库名，一个es集群中可以有多个索引库。 名称必须为小写
     * @param jsondata  json格式的数据集合
     * @return
     */
    public void createIndexResponseByBean(String indexname, String type, List<String> jsondata) {
        //创建索引库 需要注意的是.setRefresh(true)这里一定要设置,否则第一次建立索引查找不到数据
        IndexRequestBuilder requestBuilder = client.prepareIndex(indexname, type).setRefresh(true);
        for (int i = 0; i < jsondata.size(); i++) {
            requestBuilder.setSource(jsondata.get(i)).execute().actionGet();
        }

    }

    /**
     * 创建索引
     *
     * @param jsondata
     * @return
     */
    public IndexResponse createIndexResponse(String indexname, String type, String jsondata) {
        IndexResponse response = client.prepareIndex(indexname, type)
                .setSource(jsondata)
                .execute()
                .actionGet();
        return response;
    }

    /*
     *创建空索引
     */
    public void createIndexNull(String index) {
        client.admin().indices().prepareCreate(index).execute().actionGet();
    }

    /*
     * 通过ID删除记录
     */
    public DeleteResponse deleteIndexResponseById(String indexname, String type, String id) {
        DeleteResponse response = client.prepareDelete(indexname, type, id)
                .execute()
                .actionGet();
        return response;
    }

    /*
    *更新记录
     */
    public void updateIndexRecord(String index, String type, String id, Map<String, Object> params) {
        UpdateResponse response = client.prepareUpdate(index, type, id).setDoc(params)
                .execute().actionGet();
    }

    /*
     * 删除整个索引
     */
    public void deleteIndex(String index) {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
        ActionFuture<DeleteIndexResponse> response = client.admin().indices().delete(deleteIndexRequest);
    }

    public String deleteIndexAdd(String index) {
        DeleteIndexResponse deleteIndexResponse = client.admin().indices()
                .prepareDelete(index)
                .execute().actionGet();
        return deleteIndexResponse.getHeaders().toString();
    }

    public void addAlias(String index, String alias) {
        IndicesAliasesResponse response = client.admin().indices()
                .prepareAliases()
                .addAlias(index, alias)
                .execute().actionGet();
    }

    public void removeAlias(String index, String alias) {
        IndicesAliasesResponse response = client.admin().indices()
                .prepareAliases()
                .removeAlias(index, alias)
                .execute().actionGet();
    }


    /**
     * 执行搜索
     *
     * @param queryBuilder
     * @param indexname
     * @param type
     * @return
     */
    public List<Medicine> searcher(QueryBuilder queryBuilder, String indexname, String type) {
        List<Medicine> list = new ArrayList<Medicine>();
        SearchResponse searchResponse = client.prepareSearch(indexname).setTypes(type)
                .setQuery(queryBuilder)
                .execute()
                .actionGet();
        SearchHits hits = searchResponse.getHits();
        System.out.println("查询到记录数=" + hits.getTotalHits());
        SearchHit[] searchHists = hits.getHits();
        if (searchHists.length > 0) {
            for (SearchHit hit : searchHists) {
                Integer id = (Integer) hit.getSource().get("id");
                String name = (String) hit.getSource().get("name");
                String function = (String) hit.getSource().get("funciton");
                list.add(new Medicine(id, name, function));
            }
        }
        return list;
    }

    public boolean bulkAdd(String index, String type, List<String> jsonDatas) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();

        for(String jsonData : jsonDatas) {
            bulkRequest.add(client.prepareIndex(index, type).setSource(jsonData)
            );
        }

        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        return !bulkResponse.hasFailures();

    }

    /*
     *空索引设置mapping,index必须全部小写,map一旦设置不能修改
     */
    public void createMapping(String index, String type) throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .startObject(type)
                .startObject("properties")
                .startObject("name").field("type", "string").field("index", "not_analyzed").endObject()
                .endObject()
                .endObject()
                .endObject();
        PutMappingRequest mapping = Requests.putMappingRequest(index).type(type).source(builder);
        client.admin().indices().putMapping(mapping).actionGet();
    }

    /*
     *空索引设置mapping,index必须全部小写,map一旦设置不能修改
     */
    public void createMapping(String index, String type, MapContent MapContent) throws IOException {
        if (null == MapContent.getIndex()) {
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject(type)
                    .startObject("properties")
                    .startObject(MapContent.getField()).field("type", MapContent.getType()).endObject()
                    .endObject()
                    .endObject()
                    .endObject();
            PutMappingRequest mapping = Requests.putMappingRequest(index).type(type).source(builder);
            client.admin().indices().putMapping(mapping).actionGet();
        } else {
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject(type)
                    .startObject("properties")
                    .startObject(MapContent.getField()).field("type", MapContent.getType()).field("index", MapContent.getIndex()).endObject()
                    .endObject()
                    .endObject()
                    .endObject();
            PutMappingRequest mapping = Requests.putMappingRequest(index).type(type).source(builder);
            client.admin().indices().putMapping(mapping).actionGet();
        }
    }


    public SearchHit[] queryTerm(String index, String type, String field, String value, int size, int from) {
        QueryBuilder queryBuilder = QueryBuilders.termQuery(field, value);
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type)
                .setQuery(queryBuilder);
        if(size > 0) {
            searchRequestBuilder = searchRequestBuilder.setSize(size);
        }
        if(from >= 0) {
            searchRequestBuilder = searchRequestBuilder.setFrom(from);
        }
        SearchResponse searchResponse = searchRequestBuilder
                .execute()
                .actionGet();
        SearchHits hits = searchResponse.getHits();
        System.out.println("查询到记录数=" + hits.getTotalHits());
        return hits.getHits();
    }

    public SearchHit[] queryAll(String index, String type, int size, int from) {
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type)
                .setQuery(queryBuilder);
        if(size > 0) {
            searchRequestBuilder = searchRequestBuilder.setSize(size);
        }
        if(from >= 0) {
            searchRequestBuilder = searchRequestBuilder.setFrom(from);
        }
        SearchResponse searchResponse = searchRequestBuilder
                .execute()
                .actionGet();
        SearchHits hits = searchResponse.getHits();
        System.out.println("查询到记录数=" + hits.getHits().length + ",总共数量" + hits.getTotalHits());
        return hits.getHits();
    }
}