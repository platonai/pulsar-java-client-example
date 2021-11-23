package ai.platon.pulsar.examples.search.entity;

import java.util.Map;

public class ProductOverview {
    private String href;
    private float price;
    private String priceText;

    public ProductOverview(String href, float price, String priceText) {
        this.href = href;
        this.price = price;
        this.priceText = priceText;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getPriceText() {
        return priceText;
    }

    public void setPriceText(String priceText) {
        this.priceText = priceText;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "href='" + href + '\'' +
                ", price=" + price +
                ", priceText='" + priceText + '\'' +
                '}';
    }

    public static ProductOverview create(Map<String, Object> fields) {
        return new ProductOverview(
                fields.getOrDefault("href", "").toString(),
                Float.parseFloat(fields.getOrDefault("price", "0.0").toString()),
                fields.getOrDefault("pricetext", "").toString());
    }
}
