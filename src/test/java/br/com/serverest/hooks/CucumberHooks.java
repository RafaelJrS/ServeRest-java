package br.com.serverest.hooks;

import br.com.serverest.context.TestContext;
import br.com.serverest.utils.RegressionReportUpdater;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class CucumberHooks {

    private final TestContext testContext;

    public CucumberHooks(TestContext testContext) {
        this.testContext = testContext;
    }

    @Before
    public void setUpScenario() {
        testContext.resetRequestSession();
    }

    @After
    public void tearDownScenario(Scenario scenario) {
        RegressionReportUpdater.recordScenario(scenario.isFailed());
        if (scenario.isFailed() && testContext.getResponse() != null) {
            String responseBody = testContext.getResponse().asPrettyString();
            scenario.attach(responseBody, "application/json", "Payload de Resposta na Falha");
        }
        testContext.resetRequestSession();
    }

    @AfterAll
    public static void afterAllScenarios() {
        RegressionReportUpdater.updateReportIfFullSuite();
    }
}

