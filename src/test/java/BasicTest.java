import com.estest.esBasic.ElasticSearchHandler;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

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

}
