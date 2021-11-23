package ai.platon.pulsar.examples.search.scraper;

import ai.platon.pulsar.driver.Driver;
import ai.platon.pulsar.driver.ScrapeException;
import ai.platon.pulsar.driver.ScrapeResponse;
import ai.platon.pulsar.driver.utils.SQLTemplate;
import ai.platon.pulsar.examples.search.entity.ProductOverview;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class GeneralScraper {

    public static Logger logger = LoggerFactory.getLogger(GeneralScraper.class);

    private final String server;
    private final String authToken;
    private final Duration httpTimeout;
    private final int pollCount;
    private final Duration pollInterval;
    private final Driver driver;

    private int errorCode = 0;

    public GeneralScraper(String server, String authToken, Duration httpTimeout) {
        this.server = server;
        this.authToken = authToken;
        this.httpTimeout = httpTimeout;
        this.pollCount = 60;
        this.pollInterval = Duration.ofSeconds(3);
        driver = new Driver(server, authToken, httpTimeout);
    }

    public Duration scrapeTimeout() {
        return Duration.ofMillis(pollInterval.toMillis() * pollCount);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public ScrapeResponse scrape(String url, String sqlTemplate) throws InterruptedException {
        logger.info("Scraping | {}", url);

        String sql = new SQLTemplate(sqlTemplate).createSQL(url);
        String id = submit(sql, driver);
        if (id == null) {
            errorCode = 1003;
            return null;
        }

        ScrapeResponse status = driver.findById(id);
        int count = pollCount;
        while (!status.isDone() && count-- > 0) {
            Thread.sleep(pollInterval.toMillis());
            status = driver.findById(id);
        }

        return status;
    }

    public List<ScrapeResponse> scrapeAll(List<String> urls, String sqlTemplate) throws InterruptedException {
        Set<String> ids = urls.stream().map(url -> new SQLTemplate(sqlTemplate).createSQL(url))
                .map(sql -> submit(sql, driver))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        logger.info("Waiting for {} scrape tasks", ids.size());

        Set<String> finishedIds = new HashSet<>();
        List<ScrapeResponse> responses = new ArrayList<>();
        int round = 0;
        while (finishedIds.size() < ids.size() && round < pollCount && round++ < 100) {
            Thread.sleep(pollInterval.toMillis());

            Set<String> newFinishedIds = new HashSet<>();
            ids.stream().filter(id -> !finishedIds.contains(id))
                    .map(driver::findById)
                    .filter(response -> response.isDone() || response.getPageStatusCode() == 1601)
                    .forEach(response -> {
                        String id = response.getId();
                        newFinishedIds.add(id);
                        responses.add(response);
                    });

            finishedIds.addAll(newFinishedIds);

            if (round % 10 == 0 || newFinishedIds.size() > 0) {
                logger.info("Round {}.\treceived {} scrape results", round, newFinishedIds.size());
            }
        }

        logger.info("Collected {}/{} scrape responses", responses.size(), ids.size());

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
