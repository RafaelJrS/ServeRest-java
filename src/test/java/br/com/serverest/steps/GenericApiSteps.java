package br.com.serverest.steps;

import br.com.serverest.client.ApiClient;
import br.com.serverest.context.TestContext;
import br.com.serverest.utils.DataPoolReader;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.module.jsv.JsonSchemaValidator;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GenericApiSteps {

    private final TestContext testContext;
    private final ApiClient apiClient;

    public GenericApiSteps(TestContext testContext, ApiClient apiClient) {
        this.testContext = testContext;
        this.apiClient = apiClient;
    }

    @Dado("que utilizo a url da API {string}")
    public void definirVariavelUrlDaApi(String uriVariable) {
        testContext.setBaseUriVariable(uriVariable);
    }

    @Dado("que a sessao da requisicao esta limpa e inicializada")
    public void inicializarSessaoRequisicao() {
        testContext.resetRequestSession();
    }

    @Dado("que carrego o payload dinamico {string} do arquivo {string}")
    public void carregarPayloadDinamicoDoYaml(String datasetKey, String dataPoolFile) {
        Map<String, Object> dynamicData = DataPoolReader.getDynamicPayload(dataPoolFile, datasetKey);
        String jsonPayload = DataPoolReader.toJsonString(dynamicData);
        testContext.setPayload(jsonPayload);
        testContext.putState("lastPayloadMap", dynamicData);
    }

    @Dado("que possuo um usuario cadastrado a partir da massa {string} do arquivo {string}")
    public void cadastrarUsuarioEPrepararCredenciaisDeLogin(String datasetKey, String dataPoolFile) {
        apiClient.prepareRegisteredUserForLogin(dataPoolFile, datasetKey);
    }

    @Dado("que possuo um usuario recem cadastrado na variavel {string} a partir da massa {string} do arquivo {string}")
    public void cadastrarUsuarioESalvarIdNoContexto(String idContextKey, String datasetKey, String dataPoolFile) {
        apiClient.registerUserAndStoreContext(dataPoolFile, datasetKey, idContextKey);
    }

    @Dado("que reutilizo o mesmo payload do usuario recem cadastrado")
    public void reutilizarPayloadDoUsuarioRecemCadastrado() {
        Object lastUserJson = testContext.getState("LAST_REGISTERED_USER_JSON");
        assertThat(lastUserJson).as("Payload do último usuário cadastrado no contexto").isNotNull();
        testContext.setPayload(lastUserJson.toString());
    }

    @Dado("que informo o payload JSON diretamente:")
    public void informarPayloadJsonDiretamente(String rawJsonPayload) {
        testContext.setPayload(rawJsonPayload);
    }

    @Dado("que defino o parametro de rota {string} com o valor {string}")
    public void definirParametroDeRota(String paramName, String paramValue) {
        Object resolvedValue = testContext.getState(paramValue) != null
                ? testContext.getState(paramValue)
                : paramValue;
        testContext.addPathParam(paramName, resolvedValue);
    }

    @Dado("que defino os parametros de consulta:")
    public void definirParametrosDeConsulta(DataTable dataTable) {
        Map<String, String> queryMap = dataTable.asMap(String.class, String.class);
        queryMap.forEach(testContext::addQueryParam);
    }

    @Quando("envio uma requisicao {string} para a rota {string}")
    public void enviarRequisicaoParaRota(String httpVerb, String endpoint) {
        apiClient.sendRequest(httpVerb, endpoint);
    }

    @Quando("executo o metodo POST de login")
    public void executarMetodoPostDeLogin() {
        apiClient.postLogin();
    }

    @Entao("o status code da resposta deve ser {int}")
    public void validarStatusCodeDaResposta(int expectedStatusCode) {
        int actualStatusCode = testContext.getResponse().getStatusCode();
        assertThat(actualStatusCode)
                .as("Validação de HTTP Status Code")
                .isEqualTo(expectedStatusCode);
    }

    @Entao("o campo {string} na resposta JSON deve conter {string}")
    public void validarCampoRespostaJson(String jsonPath, String expectedValue) {
        String actualValue = testContext.getResponse().jsonPath().getString(jsonPath);
        assertThat(actualValue)
                .as("Validação do campo JSONPath: " + jsonPath)
                .isEqualTo(expectedValue);
    }

    @Entao("o campo {string} na resposta JSON nao deve ser nulo")
    public void validarCampoRespostaJsonNaoNulo(String jsonPath) {
        Object actualValue = testContext.getResponse().jsonPath().get(jsonPath);
        assertThat(actualValue)
                .as("O campo JSONPath '" + jsonPath + "' deve estar presente e não ser nulo")
                .isNotNull();
    }

    @Entao("o token Bearer deve ser gerado e salvo no contexto")
    public void validarTokenBearerGeradoNoContexto() {
        assertThat(testContext.getAuthToken())
                .as("Token de autenticação Bearer armazenado no TestContext")
                .isNotNull()
                .startsWith("Bearer ");
    }

    @Entao("o contrato da resposta deve respeitar o schema {string}")
    public void validarContratoJsonSchema(String schemaFilePath) {
        testContext.getResponse()
                .then()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(schemaFilePath));
    }

    @Entao("armazeno o valor do campo {string} da resposta na variavel de contexto {string}")
    public void armazenarValorDaRespostaNoContexto(String jsonPath, String contextKey) {
        Object extractedValue = testContext.getResponse().jsonPath().get(jsonPath);
        assertThat(extractedValue)
                .as("O campo extraído '" + jsonPath + "' não deve ser nulo")
                .isNotNull();
        testContext.putState(contextKey, extractedValue.toString());
    }
}
