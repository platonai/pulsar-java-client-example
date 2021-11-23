package ai.platon.pulsar.examples.search.entity;

import java.util.Map;
import java.util.Objects;

public class ProductDetail {
    private String baseUri;
    private float price;
    private String priceText;

    private String brand;
    private String model;

    private Map<String, Object> properties;

    public ProductDetail(String baseUri, float price, String priceText, String brand, String model) {
        this.baseUri = baseUri;
        this.price = price;
        this.priceText = priceText;
        this.brand = brand;
        this.model = model;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public boolean isBrandMatched() {
        return Objects.equals(brand, properties.getOrDefault("brand", "").toString());
    }

    public boolean isModelMatched() {
        return Objects.equals(brand, properties.getOrDefault("model", "").toString());
    }

    public static ProductDetail create(Map<String, Object> properties) {
        ProductDetail product = new ProductDetail(
                properties.getOrDefault("baseuri", "").toString(),
                Float.parseFloat(properties.getOrDefault("price", "0.0").toString()),
                properties.getOrDefault("pricetext", "").toString(),
                properties.getOrDefault("brand", "").toString(),
                properties.getOrDefault("model", "").toString()
        );

        product.setProperties(properties);

        return product;
    }
}
