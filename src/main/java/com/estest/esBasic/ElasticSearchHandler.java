package com.estest.esBasic;

import com.estest.bean.Medicine;
import com.estest.esDao.DataFactory;
import org.apache.lucene.queryparser.xml.builders.TermQueryBuilder;
import org.apache.lucene.search.TermQuery;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.shield.ShieldPlugin;
import org.elasticsearch.shield.authc.support.SecuredString;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

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
        //集群连接超时设置
        /*
              Settings settings = ImmutableSettings.settingsBuilder().put("client.transport.ping_timeout", "10s").build();
            client = new TransportClient(settings);
         */

//        client = new TransportClient().addTransportAddress(new InetSocketTransportAddress(ipAddress, 9300));
        String clusterName="elasticsearch";
        Settings settings = Settings.settingsBuilder()

                .put("cluster.name", clusterName)
                .put("client.transport.ping_timeout", "10s")
//                .put("shield.user", "tribe_user:tribe_user")
                .build();
        try {
            client = TransportClient.builder()
//                    .addPlugin(ShieldPlugin.class)
                    .settings(settings).build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ipAddress),9300));
//            String token = basicAuthHeaderValue("tribe_user",
//                    new SecuredString("tribe_user".toCharArray()));
//
//            client.prepareSearch().putHeader("Authorization", token).get();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * 建立索引,索引建立好之后,会在elasticsearch-0.20.6\data\elasticsearch\nodes\0创建所以你看
     * @param indexname  为索引库名，一个es集群中可以有多个索引库。 名称必须为小写
     * @param jsondata     json格式的数据集合
     *
     * @return
     */
    public void createIndexResponse(String indexname, String type, List<String> jsondata){
        //创建索引库 需要注意的是.setRefresh(true)这里一定要设置,否则第一次建立索引查找不到数据
        IndexRequestBuilder requestBuilder = client.prepareIndex(indexname, type).setRefresh(true);
        for(int i=0; i<jsondata.size(); i++){
            requestBuilder.setSource(jsondata.get(i)).execute().actionGet();
        }

    }

    /**
     * 创建索引
     * @param jsondata
     * @return
     */
    public IndexResponse createIndexResponse(String indexname, String type,String jsondata){
        IndexResponse response = client.prepareIndex(indexname, type)
                .setSource(jsondata)
                .execute()
                .actionGet();
        return response;
    }

    /**
     * 执行搜索
     * @param queryBuilder
     * @param indexname
     * @param type
     * @return
     */
    public List<Medicine>  searcher(QueryBuilder queryBuilder, String indexname, String type){
        List<Medicine> list = new ArrayList<Medicine>();
        SearchResponse searchResponse = client.prepareSearch(indexname).setTypes(type)
                .setQuery(queryBuilder)
                .execute()
                .actionGet();
        SearchHits hits = searchResponse.getHits();
        System.out.println("查询到记录数=" + hits.getTotalHits());
        SearchHit[] searchHists = hits.getHits();
        if(searchHists.length>0){
            for(SearchHit hit:searchHists){
                Integer id = (Integer)hit.getSource().get("id");
                String name =  (String) hit.getSource().get("name");
                String function =  (String) hit.getSource().get("funciton");
                list.add(new Medicine(id, name, function));
            }
        }
        return list;
    }

    public void DeleteIndexByQuery() {
//        MatchAllQueryBuilder allQueryBuilder = QueryBuilders.matchAllQuery();// 查询所有的documents
//        // 现在把blog索引post类型的索引全部删除,由于用了QueryBuilders.matchAllQuery(),匹配所有blog post下的索引
//        client.prepareDelete().setId()setQuery(allQueryBuilder).setTypes("post").execute().actionGet();
    }



    public static void main(String[] args) {
        ElasticSearchHandler esHandler = new ElasticSearchHandler();
        List<String> jsondata = DataFactory.getInitJsonData();
        String indexname = "newindex_2";
        String type = "shortType";
//        esHandler.createIndexResponse(indexname, type, jsondata);
        //查询条件
//        QueryBuilder queryBuilder = QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery("name", "感冒灵颗粒"));
        QueryBuilder queryBuilder = QueryBuilders.termQuery("name", "k");
        QueryBuilder queryBuilderId = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("id", 1));
        List<Medicine> result = esHandler.searcher(queryBuilder, indexname, type);
        for(int i=0; i<result.size(); i++){
            Medicine medicine = result.get(i);
            System.out.println("(" + medicine.getId() + ")药品名称:" +medicine.getName() + "\t\t" + medicine.getFunction());
        }
    }
}