package ai.platon.pulsar.examples.search.scraper;

import ai.platon.pulsar.driver.ScrapeResponse;
import ai.platon.pulsar.examples.search.entity.ProductOverview;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductScraper extends GeneralScraper {

    public static Logger logger = LoggerFactory.getLogger(ProductScraper.class);

    public ProductScraper(String server, String authToken, Duration httpTimeout) {
        super(server, authToken, httpTimeout);
    }

    public List<ProductOverview> scrapeProductOverviews(String url, String sqlTemplate) throws InterruptedException {
        ScrapeResponse response = scrape(url, sqlTemplate);
        assert response != null;

        Gson gson = new GsonBuilder().create();
        logger.info("List page result: \n{}", gson.toJson(response));

        List<Map<String, Object>> resultSet = response.getResultSet();
        if (resultSet == null) {
            setErrorCode(1001);
            return List.of();
        }

        return response.getResultSet()
                .stream()
                .map(ProductOverview::create)
                .collect(Collectors.toList());
    }
}
