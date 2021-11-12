package ai.platon.pulsar.examples.search;

import ai.platon.pulsar.driver.ScrapeResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MultiSearcher {
    private static String server = "platonic.fun";
    private static String authToken = "b07anchor186e206a4d991cdf87d056212b9d40e22";

    public static void main(String[] args) throws InterruptedException {
        String keyword = "口罩";

        JdSearcher jdSearcher = new JdSearcher(server, authToken);
        ScrapeResponse response = jdSearcher.search(keyword);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(response.getResultSet()));
    }
}
