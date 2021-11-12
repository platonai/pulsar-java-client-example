package ai.platon.pulsar.examples.search;

import ai.platon.pulsar.driver.ScrapeResponse;

import java.time.Duration;

/**
 * A scrape example to show how to access our services
 * Email to [ivincent.zhang@gmail.com] for your auth token
 * */
interface SearcherInterface {
    ScrapeResponse search(String keyword) throws Exception;
}
