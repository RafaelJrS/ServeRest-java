package br.com.serverest.config;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public final class RestAssuredConfigManager {

    private RestAssuredConfigManager() {
    }

    public static RequestSpecification createBaseSpecification() {
        String baseUri = ConfigurationManager.getProperty("api.base.uri");
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

