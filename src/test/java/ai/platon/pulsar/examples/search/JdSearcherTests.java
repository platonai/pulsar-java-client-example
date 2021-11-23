package ai.platon.pulsar.examples.search;

import ai.platon.pulsar.examples.search.entity.SearchOrder;
import ai.platon.pulsar.examples.search.sites.JdSearcher;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JdSearcherTests {
    private String server = "platonic.fun";
    private String authToken = "b07anchor186e206a4d991cdf87d056212b9d40e22";
    private JdSearcher jdSearcher;

    public JdSearcherTests() throws Exception {
        jdSearcher = new JdSearcher(server, authToken);
    }

    @Test
    public void whenCreateSearchUrl_thenSuccess() {
        String url = jdSearcher.createSearchUrl("3M 9501+", SearchOrder.BY_PRICE_ASC);
        url = url.substring(0, url.indexOf(" "));
        // System.out.println(url);
        assertEquals("https://search.jd.com/Search?keyword=3M+9501%2B&enc=utf-8&wq=3M+9501%2B&qrst=1&psort=2", url);

        url = jdSearcher.createSearchUrl("3M 9501+", SearchOrder.BY_SALES_DESC);
        url = url.substring(0, url.indexOf(" "));
        // System.out.println(url);
        assertEquals("https://search.jd.com/Search?keyword=3M+9501%2B&enc=utf-8&wq=3M+9501%2B&qrst=1&psort=3", url);

        url = jdSearcher.createSearchUrl("3M 9501+", SearchOrder.BY_APPLAUSE_DESC);
        url = url.substring(0, url.indexOf(" "));
        // System.out.println(url);
        assertEquals("https://search.jd.com/Search?keyword=3M+9501%2B&enc=utf-8&wq=3M+9501%2B", url);
    }
}
