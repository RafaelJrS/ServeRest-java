package br.com.serverest.client;

import br.com.serverest.context.TestContext;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ApiClient {

    private final TestContext testContext;

    public ApiClient(TestContext testContext) {
        this.testContext = testContext;
    }

    public Response sendRequest(String httpVerb, String endpoint) {
        Method method = Method.valueOf(httpVerb.trim().toUpperCase());
        RequestSpecification preparedRequest = testContext.buildPreparedRequest();
        Response response = preparedRequest.request(method, endpoint);
        testContext.setResponse(response);
        return response;
    }
}

