package ai.platon.pulsar.examples.search;

import ai.platon.pulsar.driver.*;
import ai.platon.pulsar.driver.utils.ResourceLoader;
import ai.platon.pulsar.driver.utils.SQLTemplate;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A scrape example to show how to access our services
 * Email to [ivincent.zhang@gmail.com] for your auth token
 * */
public class JdSearch {
    static String server = "platonic.fun";
    // Email to [ivincent.zhang@gmail.com] for the auth token
    static String authToken = "b07anchor186e206a4d991cdf87d056212b9d40e22";

    public static void main(String[] args) throws IOException, InterruptedException {
        String searchUrl = "https://search.jd.com/Search?keyword=%E5%8F%A3%E7%BD%A9&enc=utf-8&wq=%E5%8F%A3%E7%BD%A9&pvid=011da85b8cce401a84d17a97982aaf36";
        String searchSQLTemplate = "" +
                " select dom_abs_href(dom) as href" +
                " from load_and_select('{{url}}', '#J_goodsList li[data-sku] div.p-name a[title]')";
        String productSQLTemplate = "" +
                " select " +
                "   dom_first_text(dom, '.itemInfo-wrap .sku-name') as name," +
                "   dom_first_float(dom, '.itemInfo-wrap .p-price .price', 0.0) as price," +
                "   dom_first_text(dom, '.itemInfo-wrap .p-price .price') as priceText," +
                "   dom_first_float(dom, '.itemInfo-wrap #comment-count a', 0.0) as commentCount," +
                "   dom_first_text(dom, '.itemInfo-wrap #comment-count a') as commentCountText," +
                "   dom_first_text(dom, '.itemInfo-wrap #summary-quan .quan-item') as coupon," +
                "   dom_first_attr(dom, '.itemInfo-wrap #summary-quan .quan-item', 'title') as couponComment," +
                "   dom_first_text(dom, '.itemInfo-wrap #summary-promotion') as promotion," +
                "   dom_first_text(dom, '.itemInfo-wrap #summary-service a') as deliveryBy," +
                "   dom_first_text(dom, '.itemInfo-wrap .services') as services," +
                "   dom_all_texts(dom, '.itemInfo-wrap #choose-attrs #choose-attr-1 a') as variants" +
                " from " +
                "   load_and_select('{{url}} -i 1d -ii 7d', 'body')";

        Duration httpTimeout = Duration.ofMinutes(3);

        try (Driver driver = new Driver(server, authToken, httpTimeout)) {
            Gson gson = driver.createGson();
            String sql = new SQLTemplate(searchSQLTemplate).createSQL(searchUrl);
            String id = submit(sql, driver);
            if (id == null) {
                System.out.println("Failed to search with url " + searchUrl);
                return;
            }

            ScrapeResponse status = driver.findById(id);
            while (!status.isDone()) {
                Thread.sleep(3000);
                status = driver.findById(id);
            }
            List<Map<String, Object>> resultSet = status.getResultSet();
            if (resultSet == null) {
                System.out.println("No search result from url " + searchUrl);
                return;
            }

            System.out.println("The search result: ");
            System.out.println(gson.toJson(status));

            List<String> links = status.getResultSet()
                    .stream()
                    .map(rs -> rs.getOrDefault("href", ""))
                    .map(Object::toString)
                    .collect(Collectors.toList());

            System.out.println("Ready to visit each product: ");
            links.forEach(System.out::println);

            scrapeAll(links, productSQLTemplate, driver);
        }
    }

    private static void scrapeAll(List<String> urls, String sqlTemplate, Driver driver) {
        Set<String> ids = urls.stream().map(url -> new SQLTemplate(sqlTemplate).createSQL(url))
                .map(sql -> submit(sql, driver))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Gson gson = driver.createGson();

        List<String> finishedIds = new ArrayList<>();
        List<ScrapeResponse> responses = new ArrayList<>();
        while (finishedIds.size() < ids.size()) {
            ids.stream().filter(id -> !finishedIds.contains(id))
                    .map(driver::findById)
                    .filter(ScrapeResponse::isDone)
                    .forEach(response -> {
                        finishedIds.add(response.getId());
                        responses.add(response);
                    });
        }

        responses.forEach(response -> {
            String json = gson.toJson(response);
            System.out.println(json);
        });
    }

    private static String submit(String sql, Driver driver) {
        try {
            return driver.submit(sql, true);
        } catch (ScrapeException e) {
            e.printStackTrace();
        }
        return null;
    }
}
