package br.com.serverest.config;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public final class RestAssuredConfigManager {

    private static final String DEFAULT_URI_KEY = "api.base.uri";

    private RestAssuredConfigManager() {
    }

    public static RequestSpecification createBaseSpecification() {
        return createSpecificationForUri(ConfigurationManager.getProperty(DEFAULT_URI_KEY));
    }

    public static RequestSpecification createSpecificationForVariable(String uriKeyOrUrl) {
        String configuredUri = ConfigurationManager.getProperty(uriKeyOrUrl);
        String targetUri = (configuredUri != null && !configuredUri.isBlank()) ? configuredUri : uriKeyOrUrl;
        return createSpecificationForUri(targetUri);
    }

    private static RequestSpecification createSpecificationForUri(String baseUri) {
        return new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setConfig(buildRestAssuredConfig())
                .build();
    }

    private static RestAssuredConfig buildRestAssuredConfig() {
        int timeout = ConfigurationManager.getIntProperty("api.timeout.ms", 10000);
        HttpClientConfig httpClientConfig = HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", timeout)
                .setParam("http.socket.timeout", timeout);

        LogConfig logConfig = LogConfig.logConfig()
                .enableLoggingOfRequestAndResponseIfValidationFails();

        return RestAssured.config()
                .httpClient(httpClientConfig)
                .logConfig(logConfig);
    }
}
