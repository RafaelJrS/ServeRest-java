package br.com.serverest.client;

import br.com.serverest.context.TestContext;
import br.com.serverest.utils.DataPoolReader;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.LinkedHashMap;
import java.util.Map;

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
        captureTokenIfLoginSucceeded(method, endpoint, response);
        return response;
    }

    public Response postLogin() {
        return sendRequest("POST", "/login");
    }

    public void prepareRegisteredUserForLogin(String dataPoolFile, String datasetKey) {
        Map<String, Object> userMap = registerDynamicUser(dataPoolFile, datasetKey);
        Map<String, Object> credentials = new LinkedHashMap<>();
        credentials.put("email", userMap.get("email"));
        credentials.put("password", userMap.get("password"));
        testContext.resetRequestSession();
        testContext.setPayload(DataPoolReader.toJsonString(credentials));
    }

    public void authenticateWithDynamicUser(String dataPoolFile, String datasetKey) {
        prepareRegisteredUserForLogin(dataPoolFile, datasetKey);
        postLogin();
        testContext.resetRequestSession();
    }

    public void registerUserAndStoreContext(String dataPoolFile, String datasetKey, String idContextKey) {
        Map<String, Object> userMap = registerDynamicUser(dataPoolFile, datasetKey);
        String createdId = testContext.getResponse().jsonPath().getString("_id");
        testContext.putState(idContextKey, createdId);
        testContext.putState("LAST_REGISTERED_USER_JSON", DataPoolReader.toJsonString(userMap));
        testContext.resetRequestSession();
    }

    public void registerProductAndStoreContext(String dataPoolFile, String datasetKey, String idContextKey) {
        if (testContext.getAuthToken() == null || testContext.getAuthToken().isBlank()) {
            authenticateWithDynamicUser("usuarios-datapool.yml", "usuario_admin_valido");
        }
        Map<String, Object> productMap = DataPoolReader.getDynamicPayload(dataPoolFile, datasetKey);
        String productJson = DataPoolReader.toJsonString(productMap);
        testContext.resetRequestSession();
        testContext.setPayload(productJson);
        sendRequest("POST", "/produtos");
        testContext.putState(idContextKey, testContext.getResponse().jsonPath().getString("_id"));
        testContext.putState("LAST_REGISTERED_PRODUCT_JSON", productJson);
        testContext.resetRequestSession();
    }

    private Map<String, Object> registerDynamicUser(String dataPoolFile, String datasetKey) {
        Map<String, Object> userMap = DataPoolReader.getDynamicPayload(dataPoolFile, datasetKey);
        testContext.resetRequestSession();
        testContext.setPayload(DataPoolReader.toJsonString(userMap));
        sendRequest("POST", "/usuarios");
        return userMap;
    }

    private void captureTokenIfLoginSucceeded(Method method, String endpoint, Response response) {
        if (method == Method.POST && "/login".equalsIgnoreCase(endpoint) && response.getStatusCode() == 200) {
            String token = response.jsonPath().getString("authorization");
            if (token != null && !token.isBlank()) {
                testContext.setAuthToken(token);
                testContext.putState("AUTH_TOKEN", token);
            }
        }
    }
}
