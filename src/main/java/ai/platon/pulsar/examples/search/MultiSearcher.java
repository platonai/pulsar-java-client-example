package ai.platon.pulsar.examples.search;

import ai.platon.pulsar.examples.search.entity.ProductDetail;
import ai.platon.pulsar.examples.search.entity.SearchOrder;
import ai.platon.pulsar.examples.search.sites.JdSearcher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MultiSearcher {
    private String server = "platonic.fun";
    private String authToken = "b07anchor186e206a4d991cdf87d056212b9d40e22";
    private JdSearcher jdSearcher;

    public MultiSearcher() throws Exception {
        jdSearcher = new JdSearcher(server, authToken);
    }

    /**
     * 精准寻源
     * */
    public void searchByBrandAndModel() throws Exception {
        String brand = "3M";
        String model = "9501+";
        String keyword = brand + " " + model;

        int limitAfterSearch = 60;
        int limitAfterSort = 10;

        JdSearcher jdSearcher = new JdSearcher(server, authToken);
        // 通过SearchOrder控制：价格最优前10名, 销量最高前10名, 好评率最高前10名。
        // 其中按好评率排序，京东没有直接的搜索选项，只能先采集一批详情页，处理后排序，这个需要产品负责人进一步确认。
        // 视业务逻辑，选择排序逻辑：采集一大批详情页，通过详情页字段匹配，然后排序。
        List<ProductDetail> products = jdSearcher.search(keyword, SearchOrder.BY_SALES_DESC, brand, model, limitAfterSearch)
                .stream()
                .limit(limitAfterSort)
                .collect(Collectors.toList());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(products));
    }

    /**
     * 模糊寻源
     * */
    public void searchByKeyword() throws Exception {
        String keyword = "口罩";
        int limitBeforeScrape = 60;
        int limitAfterSort = 10;

        // 视业务逻辑，选择排序逻辑：采集一大批详情页，通过详情页字段匹配，然后排序
        List<ProductDetail> products = jdSearcher.search(keyword, SearchOrder.BY_SALES_DESC, limitBeforeScrape)
                .stream()
                .limit(limitAfterSort)
                .collect(Collectors.toList());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(products));
    }

    /**
     * 热销Top
     *
     * 视业务逻辑，选择排序逻辑：
     * 1. 取搜索结果页的前10项
     * 2. 取搜索结果页第一页，采集详情页，通过详情页字段匹配，然后排序，最后取结果的前10项
     * 3. 同2，但取搜索结果页前 n 页，这个方案最准确，但需要综合考虑成本、响应时间和用户体验
     *
     * 备注：店铺信息后续处理
     * */
    public void topSales() throws Exception {
        String keyword = "口罩";
        int limitBeforeScrape = 60;
        int limitAfterSort = 10;

        List<ProductDetail> products = jdSearcher.search(keyword, SearchOrder.BY_SALES_DESC, limitBeforeScrape)
                .stream()
                .limit(limitAfterSort)
                .collect(Collectors.toList());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(products));
    }

    /**
     * 品牌巡检
     * */
    public void brandMonitoring() throws Exception {
        String keyword = "御林铁卫";
        int limitBeforeScrape = 60;

        List<ProductDetail> products = jdSearcher.search(keyword, SearchOrder.DEFAULT, limitBeforeScrape);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(products));
    }

    public static void main(String[] args) throws Exception {
        MultiSearcher searcher = new MultiSearcher();
//        searcher.searchByBrandAndModel();
//        searcher.searchByKeyword();
        searcher.brandMonitoring();
    }
}
