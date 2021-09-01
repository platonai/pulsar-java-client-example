package ai.platon.pulsar.examples;

import ai.platon.pulsar.driver.*;
import ai.platon.pulsar.driver.utils.ResourceLoader;
import ai.platon.pulsar.driver.utils.SQLTemplate;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Scrape {
    public static void main(String[] args) throws IOException {
        String server = "master";
        String authToken = "b12yCTcfWnw0dFS767eadcea57a6ce4077348b7b3699578";
        Duration httpTimeout = Duration.ofMinutes(3);

        List<String> urls = ResourceLoader.INSTANCE.readAllLines("sites/amazon/asin/urls.txt")
                .stream().limit(10).collect(Collectors.toList());
        String sqlTemplate =
            "select\n" +
            "   dom_first_text(dom, '#productTitle') as `title`,\n" +
            "   dom_first_text(dom, '#price tr td:contains(List Price) ~ td') as `listprice`,\n" +
            "   dom_first_text(dom, '#price tr td:matches(^Price) ~ td, #price_inside_buybox') as `price`,\n" +
            "   array_join_to_string(dom_all_texts(dom, '#wayfinding-breadcrumbs_container ul li a'), '|') as `categories`,\n" +
            "   dom_base_uri(dom) as `baseUri`\nfrom\n" +
            "   load_and_select('{{url}} -i 1h', ':root')\n";

        try (Driver driver = new Driver(server, authToken, httpTimeout)) {
            Set<String> ids = new HashSet<>();
            for (String url : urls) {
                String sql = new SQLTemplate(sqlTemplate).createSQL(url);
                String id = null;
                try {
                    id = driver.submit(sql, true);
                } catch (ScrapeException e) {
                    e.printStackTrace();
                }
                if (id == null) {
                    break;
                }
                ids.add(id);
            }

            if (ids.isEmpty()) {
                System.out.println("No id collected");
                return;
            }

            Path path = Files.createTempFile("pulsar-", ".txt");
            Files.write(path, ids);
            System.out.println("Ids are written to " + path);

            Gson gson = driver.createGson();

            // we may want to check the status of a scrape task with a specified id
            ScrapeResponse status = driver.findById(ids.iterator().next());
            System.out.println(gson.toJson(status));

            // we may want to check our dashboard
            Dashboard dashboard = driver.dashboard();
            System.out.println(gson.toJson(dashboard));

            // we download all the scrape results
            Page<CompactedScrapeResponse> results = driver.download(0, 10);
            System.out.println(gson.toJson(results));
        }
    }
}
