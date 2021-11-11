package ai.platon.pulsar.examples;

import ai.platon.pulsar.driver.Driver;
import ai.platon.pulsar.driver.ScrapeException;
import ai.platon.pulsar.driver.ScrapeResponse;
import ai.platon.pulsar.driver.utils.ResourceLoader;
import ai.platon.pulsar.driver.utils.SQLTemplate;
import com.google.gson.Gson;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A scrape example to show how to access our services
 * Email to [ivincent.zhang@gmail.com] for your auth token
 * */
public class ScrapeAndPoll {
//    static String server = "platonic.fun";
    static String server = "crawl0";
    // Email to [ivincent.zhang@gmail.com] for the auth token
    static String authToken = "b12yCTcfWnw0dFS767eadcea57a6ce4077348b7b3699578";

    public static void main(String[] args) throws IOException {
        List<String> urls = ResourceLoader.INSTANCE.readAllLines("sites/amazon/asin/urls.txt")
                .stream().skip(1).limit(5).collect(Collectors.toList());
        String sqlTemplate =
            "select\n" +
            "   dom_first_text(dom, '#productTitle') as `title`,\n" +
            "   dom_first_text(dom, '#price tr td:contains(List Price) ~ td') as `listprice`,\n" +
            "   dom_first_text(dom, '#price tr td:matches(^Price) ~ td, #price_inside_buybox') as `price`,\n" +
            "   array_join_to_string(dom_all_texts(dom, '#wayfinding-breadcrumbs_container ul li a'), '|') as `categories`,\n" +
            "   dom_base_uri(dom) as `baseUri`\nfrom\n" +
            "   load_and_select('{{url}} -i 7d', ':root')\n";

        Duration httpTimeout = Duration.ofMinutes(3);

        try (Driver driver = new Driver(server, authToken, httpTimeout)) {
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
