package ai.platon.pulsar.examples.search.scraper;

import ai.platon.pulsar.examples.search.entity.ProductDetail;

import java.util.List;

/**
 * The searcher interface
 * */
public interface SiteScraper {
    List<ProductDetail> search(String keyword, int limit) throws Exception;
}
