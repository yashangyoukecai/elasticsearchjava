import com.estest.bean.Content;
import com.estest.esBasic.ElasticSearchHandler;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

/**
 * Created by wklmogujie on 16/1/7.
 */
public class BasicTest {
    @Test
    public void TestDelete() {
        ElasticSearchHandler esHandler = new ElasticSearchHandler();
        DeleteResponse response = esHandler.deleteIndexResponseById("blog","article","AVIWM_MvOcGLzJiDqIyK");
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

            Content content1 = new Content();
            content1.setField("name");
            content1.setType("string");
            content1.setIndex("not_analyzed");
            esHandler.createMapping("maptestcontent", "book", content1);
            Content content2 = new Content();
            content2.setField("price");
            content2.setType("long");
            esHandler.createMapping("maptestcontent", "book", content2);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
