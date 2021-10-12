package ai.platon.pulsar.examples.harvest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * A scrape example to show how to access our services
 * Email to [ivincent.zhang@gmail.com] for your auth token
 * */
public class HarvestArticle {
    static String server = "platonic.fun";
    // Email to [ivincent.zhang@gmail.com] for the auth token
    static String authToken = "b12yCTcfWnw0dFS767eadcea57a6ce4077348b7b3699578";

    public static void main(String[] args) throws IOException, URISyntaxException {
        String targetUrl = "http://www.news.cn/world/2021-10/08/c_1127935358.htm";
        String htmlContent = readResourceAsString("news/news.cn/c_1127935358.html");

        URL url = new URL("http://" + server + ":8182/api/xx/h/article");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        Gson gson = new GsonBuilder().create();
        HarvestRequest request = new HarvestRequest(authToken, targetUrl, htmlContent, true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = gson.toJson(request).getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        String response = readStreamAsString(connection.getInputStream());
        System.out.println(response);
    }

    private static String readResourceAsString(String resource) throws URISyntaxException, IOException {
        return new String(Files.readAllBytes(
                Paths.get(HarvestArticle.class.getClassLoader().getResource(resource).toURI())));
    }

    private static String readStreamAsString(InputStream is) {
        StringBuilder response = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.toString();
    }
}
