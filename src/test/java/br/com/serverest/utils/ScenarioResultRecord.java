package br.com.serverest.utils;

import br.com.serverest.context.TestContext;
import io.cucumber.java.Scenario;
import io.restassured.response.Response;

import java.util.Collection;

public record ScenarioResultRecord(
        int index,
        String tag,
        String domain,
        String httpVerb,
        String endpoint,
        String scenarioName,
        String qualityFront,
        int statusCode,
        long apiTimeMs,
        long durationMs,
        boolean passed) {

    public static ScenarioResultRecord fromScenario(
            int index, Scenario scenario, TestContext context, long durationMs) {
        Collection<String> tags = scenario.getSourceTagNames();
        Response resp = context.getResponse();
        String verb = String.valueOf(context.getState("LAST_HTTP_VERB") != null ? context.getState("LAST_HTTP_VERB") : "HTTP");
        String route = String.valueOf(context.getState("LAST_ENDPOINT") != null ? context.getState("LAST_ENDPOINT") : "/");
        int code = (resp != null) ? resp.getStatusCode() : 0;
        long apiTime = (resp != null) ? resp.getTime() : 0L;
        return new ScenarioResultRecord(
                index, resolveSpecificTag(tags), resolveDomain(tags), verb, route,
                scenario.getName(), resolveQualityFront(tags, scenario.getName(), code),
                code, apiTime, durationMs, !scenario.isFailed());
    }

    private static String resolveDomain(Collection<String> tags) {
        if (tags.contains("@login")) return "Login (/login)";
        if (tags.contains("@usuarios")) return "Usuários (/usuarios)";
        if (tags.contains("@produtos")) return "Produtos (/produtos)";
        if (tags.contains("@carrinhos")) return "Carrinhos (/carrinhos)";
        return "Geral";
    }

    private static String resolveSpecificTag(Collection<String> tags) {
        return tags.stream()
                .filter(t -> !t.equals("@api") && !t.equals("@regressivo") && !t.equals("@smoke")
                        && !t.equals("@login") && !t.equals("@usuarios") && !t.equals("@produtos") && !t.equals("@carrinhos"))
                .findFirst()
                .orElse("@cenario");
    }

    private static String resolveQualityFront(Collection<String> tags, String name, int statusCode) {
        String lower = name.toLowerCase();
        if (lower.contains("estoque") && lower.contains("validar baixa")) return "7. Prova Matemática E2E";
        if (statusCode == 401 || statusCode == 403 || lower.contains("token") || lower.contains("sem permissao")) return "4. Segurança & RBAC";
        if (lower.contains("em uso") || lower.contains("com carrinho")) return "5. Integridade Referencial";
        if (lower.contains("zero") || lower.contains("negativo") || lower.contains("16 caracteres") || lower.contains("formato") || lower.contains("em branco") || lower.contains("booleana")) return "6. Valor Limite & Entrada";
        if (statusCode == 400 || lower.contains("inexistente") || lower.contains("duplicado") || lower.contains("sem carrinho")) return "3. Regra de Negócio";
        return "1. Funcional / CRUD / Schema";
    }
}
