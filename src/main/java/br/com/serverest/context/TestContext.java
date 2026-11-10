package br.com.serverest.context;

import br.com.serverest.config.RestAssuredConfigManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

public class TestContext {

    private RequestSpecification requestSpec;
    private Response response;
    private Object payload;
    private final Map<String, Object> pathParams = new HashMap<>();
    private final Map<String, Object> queryParams = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, Object> scenarioState = new HashMap<>();

    public TestContext() {
        resetRequestSession();
    }

    public void resetRequestSession() {
        this.requestSpec = RestAssured.given().spec(RestAssuredConfigManager.createBaseSpecification());
        this.payload = null;
        this.pathParams.clear();
        this.queryParams.clear();
        this.headers.clear();
    }

    public RequestSpecification buildPreparedRequest() {
        RequestSpecification spec = RestAssured.given().spec(this.requestSpec);
        if (!headers.isEmpty()) {
            spec.headers(headers);
        }
        if (!pathParams.isEmpty()) {
            spec.pathParams(pathParams);
        }
        if (!queryParams.isEmpty()) {
            spec.queryParams(queryParams);
        }
        return (payload != null) ? spec.body(payload) : spec;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public void addPathParam(String key, Object value) {
        this.pathParams.put(key, value);
    }

    public void addQueryParam(String key, Object value) {
        this.queryParams.put(key, value);
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public void putState(String key, Object value) {
        this.scenarioState.put(key, value);
    }

    public Object getState(String key) {
        return this.scenarioState.get(key);
    }
}

