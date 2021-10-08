package ai.platon.pulsar.examples.harvest;

public class HarvestRequest {
    private String authToken;
    private String portalUrl;
    private String htmlContent;

    public HarvestRequest(String authToken, String portalUrl, String htmlContent) {
        this.authToken = authToken;
        this.portalUrl = portalUrl;
        this.htmlContent = htmlContent;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getPortalUrl() {
        return portalUrl;
    }

    public String getHtmlContent() {
        return htmlContent;
    }
}
