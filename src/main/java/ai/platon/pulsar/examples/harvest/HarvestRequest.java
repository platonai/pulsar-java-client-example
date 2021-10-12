package ai.platon.pulsar.examples.harvest;

public class HarvestRequest {
    private String authToken;
    private String portalUrl;
    private String htmlContent;
    private Boolean withHtml;

    public HarvestRequest(String authToken, String portalUrl, String htmlContent, Boolean withHtml) {
        this.authToken = authToken;
        this.portalUrl = portalUrl;
        this.htmlContent = htmlContent;
        this.withHtml = withHtml;
    }

    public HarvestRequest(String authToken, String portalUrl, String htmlContent) {
        this(authToken, portalUrl, htmlContent, false);
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

    public Boolean getWithHtml() {
        return withHtml;
    }
}
