package ai.platon.pulsar.examples.search.sites;

import ai.platon.pulsar.driver.ScrapeResponse;
import ai.platon.pulsar.examples.search.search.ProductSearcher;
import ai.platon.pulsar.examples.search.entity.SearchOrder;
import ai.platon.pulsar.examples.search.search.SiteSearcher;
import ai.platon.pulsar.examples.search.entity.ProductDetail;
import ai.platon.pulsar.examples.search.entity.ProductOverview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JdSearcher implements SiteSearcher {

    public static Logger logger = LoggerFactory.getLogger(JdSearcher.class);

    private String searchUrlTemplate = "https://search.jd.com/Search?keyword={{keyword}}&enc=utf-8&wq={{keyword}}";
    private String searchArgs = "-expires 1d -nJitRetry 3 -ignoreFailure";
    private String productUrlArgs = "-expires 7d";
    private int searchPageSize = 60;

    private String searchSQLTemplate;
    private String productSQLTemplate;
    private Duration httpTimeout = Duration.ofMinutes(3);
    private ProductSearcher searcher;

    public JdSearcher(String server, String authToken) throws Exception {
        URL url = JdSearcher.class.getClassLoader().getResource("sites/jd/x-search.sql");
        assert url != null;
        searchSQLTemplate = Files.readString(new File(url.toURI()).toPath());

        url = JdSearcher.class.getClassLoader().getResource("sites/jd/x-item.sql");
        assert url != null;
        productSQLTemplate = Files.readString(new File(url.toURI()).toPath());

        searcher = new ProductSearcher(server, authToken, httpTimeout);
    }

    public JdSearcher(String server, String authToken, Duration indexPageExpireTime, Duration itemPageExpireTime) throws Exception {
        this(server, authToken);
    }

    public String createSearchUrl(String keyword, SearchOrder order) {
        try {
            keyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            logger.warn("Unexpected encoding. The scraping will be continue and leave the encoding problem to the server", e);
        }

        String searchUrl = searchUrlTemplate.replaceAll("\\{\\{keyword}}", keyword);

        if (order == SearchOrder.BY_PRICE_ASC) {
            searchUrl += "&qrst=1&psort=2";
        } else if (order == SearchOrder.BY_SALES_DESC) {
            searchUrl += "&qrst=1&psort=3";
        } else if (order == SearchOrder.BY_APPLAUSE_DESC) {
            searchUrl = searchUrl;
        }

        searchUrl += " " + searchArgs.trim();

        return searchUrl;
    }

    public List<ProductDetail> search(String keyword, int limit) throws Exception {
        return search(keyword, SearchOrder.DEFAULT, limit);
    }

    /**
     * 在目标网站中按“keyword”搜索，按照制定次序排序，选择前limit个产品链接，并进一步采集这limit个产品详情页
     * */
    public List<ProductDetail> search(String keyword, SearchOrder order, int limit) throws Exception {
        String searchUrl = createSearchUrl(keyword, order);
        List<ProductOverview> productOverviews = searcher.search(searchUrl, searchSQLTemplate);

        if (limit > searchPageSize) {
            // if we want to crawl from other pages
        }

        List<String> productUrls = productOverviews.stream()
                .limit(limit)
                .map(ProductOverview::getHref)
                .map(href -> href + " " + productUrlArgs.trim())
                .collect(Collectors.toList());
        List<ScrapeResponse> responses = searcher.scrapeAll(productUrls, productSQLTemplate);
        List<ProductDetail> products = responses.stream()
                .map(ScrapeResponse::getResultSet)
                .filter(Objects::nonNull)
                .filter(l -> l.size() > 0)
                .map(l -> l.get(0))
                .map(ProductDetail::create)
                .collect(Collectors.toList());

        return products;
    }

    /**
     * 在目标网站中按“keyword”搜索，按照制定次序排序，选择前limit个产品链接
     * 进一步采集这limit个产品详情页，按照brand和model筛选
     * */
    public List<ProductDetail> search(
            String keyword, SearchOrder order, String brand, String model, int limit
    ) throws Exception {
        List<ProductDetail> brandMatchedProducts = search(keyword, order, limit).stream()
                .filter(p -> Objects.equals(p.getBrand(), brand))
                .collect(Collectors.toList());

        logger.info("Total {} products matches the brand {}", brandMatchedProducts.size(), brand);

        List<ProductDetail> modelMatchedProducts = brandMatchedProducts.stream()
                .filter(p -> Objects.equals(p.getModel(), model))
                .collect(Collectors.toList());

        List<ProductDetail> modelNotMatchedProducts = brandMatchedProducts.stream()
                .filter(p -> !Objects.equals(p.getModel(), model))
                .collect(Collectors.toList());

        List<ProductDetail> products = new LinkedList<>();
        products.addAll(modelMatchedProducts);
        products.addAll(modelNotMatchedProducts);

        return products;
    }
}
