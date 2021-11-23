package ai.platon.pulsar.examples.search;

import ai.platon.pulsar.examples.search.db.Db;
import ai.platon.pulsar.examples.search.entity.ProductDetail;
import ai.platon.pulsar.examples.search.entity.SearchOrder;
import ai.platon.pulsar.examples.search.sites.JdScraper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.math.NumberUtils;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MultiScraper {
    private String server = "platonic.fun";
    private String authToken = "b07anchor186e206a4d991cdf87d056212b9d40e22";
    private JdScraper jdScraper;
    private final Db db;
    private final Connection connection;

    private final String insertSQLTemplate = "" +
            "INSERT INTO `gongzhitech`.`integrated_products` " +
            "(" +
            "`site`, " +
            "`big_img_url`, " +
            "`product_name`, " +
            "`brand`, " +
            "`model`, " +
            "`specification`, " +
            "`material`, " +
            "`price`, " +
            "`price_text`, " +
            "`min_amount_to_buy`, " +
            "`max_amount_to_buy`, " +
            "`inventory_amount`, " +
            "`sales_volume`, " +
            "`favorable_rate`, " +
            "`good_reviews`, " +
            "`normal_reviews`, " +
            "`bad_reviews`, " +
            "`shop_name`, " +
            "`shop_url`, " +
            "`shop_location`, " +
            "`shop_tel`, " +
            "`shop_scores`, " +
            "`express_fee`) " +
            " VALUES " +
            " ( " +
            "<{site: }>, " +
            "<{big_img_url: }>, " +
            "<{product_name: }>, " +
            "<{brand: }>, " +
            "<{model: }>, " +
            "<{specification: }>, " +
            "<{material: }>, " +
            "<{price: }>, " +
            "<{price_text: }>, " +
            "<{min_amount_to_buy: }>, " +
            "<{max_amount_to_buy: }>, " +
            "<{inventory_amount: }>, " +
            "<{sales_volume: }>, " +
            "<{favorable_rate: }>, " +
            "<{good_reviews: }>, " +
            "<{normal_reviews: }>, " +
            "<{bad_reviews: }>, " +
            "<{shop_name: }>, " +
            "<{shop_url: }>, " +
            "<{shop_location: }>, " +
            "<{shop_tel: }>, " +
            "<{shop_scores: }>, " +
            "<{express_fee: }>); ";
    List<String> integerFields = List.of(
            "min_amount_to_buy", "max_amount_to_buy", "inventory_amount", "sales_volume",
            "good_reviews", "normal_reviews", "bad_reviews"
    );

    public MultiScraper() throws Exception {
        jdScraper = new JdScraper(server, authToken);
        db = new Db();
        connection = db.createConnection();
    }

    public void loadFromCategories() throws Exception {
        List<String> categoryUrls = List.of(
                "https://list.jd.com/list.html?cat=9855,9858,9923",
                "https://list.jd.com/list.html?cat=9855,9858,9925",
                "https://list.jd.com/list.html?cat=9855%2C9858%2C13753&ev=13971_103024%5E&cid3=13753",
                "https://list.jd.com/list.html?cat=9855,9858,13821",
                "https://list.jd.com/list.html?cat=9855,9858,9925",
                "https://list.jd.com/list.html?cat=9855,9858,13822"
        );

        for (String categoryUrl : categoryUrls) {
            List<ProductDetail> products = jdScraper.loadFromCategory(categoryUrl);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.println(gson.toJson(products));

            Statement stmt = connection.createStatement();
            for (ProductDetail product : products) {
                String sql = insertSQLTemplate;
                for (Map.Entry<String, Object> e : product.getProperties().entrySet()) {
                    String key = e.getKey();
                    String value = e.getValue().toString().replace("'", "");
                    if (integerFields.contains(key)) {
                        if (!NumberUtils.isDigits(value)) {
                            value = "-1";
                        }
                        sql = sql.replace("<{" + key + ": }>", value);
                    } else {
                        sql = sql.replace("<{" + key + ": }>", "'" + value + "'");
                    }
                }
                for (String key : integerFields) {
                    sql = sql.replace("<{" + key + ": }>", "-1");
                }
                sql = sql.replaceAll("<\\{.+: }>", "''");
                assert !sql.contains("<{");
                // System.out.println(sql.replaceAll("\n", ""));
                stmt.execute(sql);
            }
            stmt.close();
        }
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

        JdScraper jdSearcher = new JdScraper(server, authToken);
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
        List<ProductDetail> products = jdScraper.search(keyword, SearchOrder.BY_SALES_DESC, limitBeforeScrape)
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

        List<ProductDetail> products = jdScraper.search(keyword, SearchOrder.BY_SALES_DESC, limitBeforeScrape)
                .stream()
                .limit(limitAfterSort)
                .collect(Collectors.toList());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(products));
    }

    /**
     * 品牌巡检
     *
     * 备注：大多数平台不提供销售量数据
     * */
    public void brandMonitoring() throws Exception {
        String keyword = "御林铁卫";
        int limitBeforeScrape = 60;

        List<ProductDetail> products = jdScraper.search(keyword, SearchOrder.DEFAULT, limitBeforeScrape);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(products));
    }

    public static void main(String[] args) throws Exception {
        MultiScraper scraper = new MultiScraper();
        // scraper.loadFromCategories();
    }
}
