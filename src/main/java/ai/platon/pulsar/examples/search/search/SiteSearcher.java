package ai.platon.pulsar.examples.search.search;

import ai.platon.pulsar.examples.search.entity.ProductDetail;

import java.util.List;

/**
 * The searcher interface
 * */
public interface SiteSearcher {
    List<ProductDetail> search(String keyword, int limit) throws Exception;
}
