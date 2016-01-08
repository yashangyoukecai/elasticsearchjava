import com.estest.bean.MapContent;
import com.estest.bean.Medicine;
import com.estest.esBasic.ElasticSearchHandler;
import com.estest.esDao.DataFactory;
import com.google.common.collect.Maps;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Created by wklmogujie on 16/1/7.
 */
public class BasicTest {
    @Test
    public void termQueryTest() {
        //  must/and,   mustnot/<>,   should/or
        ElasticSearchHandler esHandler = new ElasticSearchHandler();
        List<String> jsondata = DataFactory.getInitJsonData();
        String indexname = "newindex_2";
        String type = "shortType";
        esHandler.createIndexResponseByBean(indexname, type, jsondata);
        //查询条件
//        QueryBuilder queryBuilder = QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery("name", "感冒灵颗粒"));
        QueryBuilder queryBuilder = QueryBuilders.termQuery("name", "k");
        QueryBuilder queryBuilderId = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("id", 1));
        List<Medicine> result = esHandler.searcher(queryBuilder, indexname, type);
        for (int i = 0; i < result.size(); i++) {
            Medicine medicine = result.get(i);
            System.out.println("(" + medicine.getId() + ")药品名称:" + medicine.getName() + "\t\t" + medicine.getFunction());
        }
    }

    @Test
    public void TestDelete() {
        ElasticSearchHandler esHandler = new ElasticSearchHandler();
        DeleteResponse response = esHandler.deleteIndexResponseById("blog", "article", "AVIWM_MvOcGLzJiDqIyK");
        System.out.println("delete result :" + response.isFound());
    }

    @Test
    public void TestBulkAdd() {
        ElasticSearchHandler esHandler = new ElasticSearchHandler();
        boolean response = esHandler.bulkAdd();
        System.out.println("BulkAdd result :" + response);
    }

    @Test
    public void testDeteleIndex() {
        ElasticSearchHandler esHandler = new ElasticSearchHandler();
        esHandler.deleteIndex("delete");
    }

    @Test
    public void testDeteleIndexAdd() {
        ElasticSearchHandler esHandler = new ElasticSearchHandler();
        String re = esHandler.deleteIndexAdd("delete");
        System.out.println("re:" + re);
    }

    @Test
    public void testMapping() {
        ElasticSearchHandler esHandler = new ElasticSearchHandler();
        esHandler.createIndexNull("maptest");
        try {
            esHandler.createMapping("maptest","type1");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testMappingContent() {
        ElasticSearchHandler esHandler = new ElasticSearchHandler();
        esHandler.createIndexNull("maptestcontent");
        try {
            MapContent mapContent1 = new MapContent();
            mapContent1.setField("name");
            mapContent1.setType("string");
            mapContent1.setIndex("not_analyzed");
            esHandler.createMapping("maptestcontent", "book", mapContent1);
            MapContent mapContent2 = new MapContent();
            mapContent2.setField("price");
            mapContent2.setType("long");
            esHandler.createMapping("maptestcontent", "book", mapContent2);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testUpdate() {
        Map<String, Object> params = Maps.newHashMap();
        params.put("type1", "未闻");
        ElasticSearchHandler esHandler = new ElasticSearchHandler();
        esHandler.updateIndexRecord("maptest", "type1", "2", params);
    }

    @Test
    public void testAddAlias() {
        ElasticSearchHandler esHandler = new ElasticSearchHandler();
        esHandler.addAlias("maptest_v1", "map");
    }

    @Test
    public void testRemoveAlias() {
        ElasticSearchHandler esHandler = new ElasticSearchHandler();
        esHandler.removeAlias("maptest", "maptest_v2");
    }

    @Test
    public void trans() {
        //迁移index
        ElasticSearchHandler esHandler = new ElasticSearchHandler();
        SearchHit[] searchHists = esHandler.queryAll("maptest", "type1");
        if(searchHists.length > 0) {
            for (SearchHit hit : searchHists) {
                String re = (String) hit.getSource().get("type1");
                String jsonString = "{\"type1\":\""+ re +"\"}";
                esHandler.createIndexResponse("maptest_v1", "type1", jsonString);
            }
        }
        System.out.println("end");
    }

    @Test
    public void queryAlias() {
        //alias查询到所有的alias相同的记录
        ElasticSearchHandler elasticSearchHandler = new ElasticSearchHandler();
        elasticSearchHandler.queryAll("map","type1");
    }
}
