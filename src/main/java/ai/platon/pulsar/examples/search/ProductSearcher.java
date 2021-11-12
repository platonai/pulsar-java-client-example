package ai.platon.pulsar.examples.search;

import ai.platon.pulsar.driver.Driver;
import ai.platon.pulsar.driver.ScrapeException;
import ai.platon.pulsar.driver.ScrapeResponse;
import ai.platon.pulsar.driver.utils.SQLTemplate;
import ai.platon.pulsar.examples.search.entity.ProductOverview;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class ProductSearcher {
    private final String server;
    private final String authToken;
    private final Duration httpTimeout;
    private final Driver driver;

    private int errorCode = 0;

    public ProductSearcher(String server, String authToken, Duration httpTimeout) {
        this.server = server;
        this.authToken = authToken;
        this.httpTimeout = httpTimeout;
        driver = new Driver(server, authToken, httpTimeout);
    }

    public String toJson(Object src) {
        return driver.createGson().toJson(src);
    }

    public ScrapeResponse search(String searchUrl, String searchSQLTemplate, String productSQLTemplate) throws InterruptedException {
        ScrapeResponse response = scrape(searchUrl, searchSQLTemplate, driver);
        assert response != null;
        List<Map<String, Object>> resultSet = response.getResultSet();
        if (resultSet == null) {
            errorCode = 1001;
            return null;
        }

        List<ProductOverview> productOverviews = response.getResultSet()
                .stream()
                .map(rs -> new ProductOverview(
                        rs.getOrDefault("href", "").toString(),
                        Float.parseFloat(rs.getOrDefault("price", "0.0").toString()),
                        rs.getOrDefault("pricetext", "").toString()))
                .collect(Collectors.toList());

        ProductOverview bestProduct = productOverviews.stream()
                .min(Comparator.comparing(ProductOverview::getPrice))
                .orElse(null);

        if (bestProduct != null) {
            String href = bestProduct.getHref();
            return scrape(href, productSQLTemplate, driver);
        }

        errorCode = 1002;

        return null;
    }

    public ScrapeResponse scrape(String url, String sqlTemplate, Driver driver) throws InterruptedException {
        String sql = new SQLTemplate(sqlTemplate).createSQL(url);
        String id = submit(sql, driver);
        if (id == null) {
            errorCode = 1003;
            return null;
        }

        ScrapeResponse status = driver.findById(id);
        int i = 0;
        while (!status.isDone() && i++ < 60) {
            Thread.sleep(3000);
            status = driver.findById(id);
        }

        return status;
    }

    public List<ScrapeResponse> scrapeAll(List<String> urls, String sqlTemplate, Driver driver) {
        Set<String> ids = urls.stream().map(url -> new SQLTemplate(sqlTemplate).createSQL(url))
                .map(sql -> submit(sql, driver))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

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

        return responses;
    }

    public String submit(String sql, Driver driver) {
        try {
            return driver.submit(sql, true);
        } catch (ScrapeException e) {
            e.printStackTrace();
        }
        return null;
    }
}
