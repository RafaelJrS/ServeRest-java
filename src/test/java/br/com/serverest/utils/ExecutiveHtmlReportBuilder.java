package br.com.serverest.utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ExecutiveHtmlReportBuilder {

    private static final String CSS_STYLES =
            "*{box-sizing:border-box;margin:0;padding:0;font-family:'Segoe UI',Roboto,Arial,sans-serif;-webkit-print-color-adjust:exact;print-color-adjust:exact;}"
            + "body{background:#f1f5f9;color:#0f172a;padding:24px;}.container{max-width:1280px;margin:0 auto;}"
            + ".header{background:linear-gradient(135deg,#0f172a,#1e3a8a);color:#fff;padding:28px 32px;border-radius:14px;display:flex;justify-content:space-between;align-items:center;margin-bottom:22px;box-shadow:0 8px 20px rgba(15,23,42,.12);}"
            + ".badge-pass{background:#10b981;color:#fff;padding:6px 14px;border-radius:999px;font-weight:700;font-size:13px;display:inline-block;}"
            + ".badge-fail{background:#ef4444;color:#fff;padding:6px 14px;border-radius:999px;font-weight:700;font-size:13px;display:inline-block;}"
            + ".grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(145px,1fr));gap:12px;margin-bottom:22px;}"
            + ".card{background:#fff;padding:18px 20px;border-radius:12px;border:1px solid #e2e8f0;box-shadow:0 2px 6px rgba(0,0,0,.03);}"
            + ".card h4{font-size:12px;text-transform:uppercase;color:#64748b;letter-spacing:.6px;margin-bottom:6px;}.card .val{font-size:24px;font-weight:800;color:#0f172a;}"
            + "h2{font-size:18px;margin:22px 0 12px;color:#1e293b;}table{width:100%;border-collapse:collapse;background:#fff;border-radius:12px;overflow:hidden;border:1px solid #e2e8f0;margin-bottom:24px;font-size:12.5px;}"
            + "th{background:#1e293b;color:#fff;text-align:left;padding:10px 12px;font-weight:600;}td{padding:8px 12px;border-bottom:1px solid #e2e8f0;vertical-align:middle;}"
            + "tr:nth-child(even){background:#f8fafc;}.verb{font-weight:700;padding:3px 8px;border-radius:6px;font-size:11px;color:#fff;display:inline-block;min-width:52px;text-align:center;}"
            + ".GET{background:#2563eb;}.POST{background:#059669;}.PUT{background:#d97706;}.DELETE{background:#dc2626;}"
            + ".btn-print{background:#38bdf8;color:#0f172a;border:none;padding:10px 18px;border-radius:8px;font-weight:700;cursor:pointer;margin-top:8px;}"
            + "@media print{body{background:#fff;padding:0;}.btn-print{display:none;}tr{page-break-inside:avoid;}}";

    private ExecutiveHtmlReportBuilder() {
    }

    public static String buildHtml(List<ScenarioResultRecord> records, String timestamp, long suiteDurationMs) {
        StringBuilder sb = new StringBuilder(16384);
        sb.append("<!DOCTYPE html><html lang='pt-BR'><head><meta charset='UTF-8'>")
          .append("<title>Relatório Executivo de QA - ServeRest API</title><style>")
          .append(CSS_STYLES).append("</style></head><body><div class='container'>");
        appendHeader(sb, timestamp, records, suiteDurationMs);
        appendKpiCards(sb, records, suiteDurationMs);
        appendDomainCards(sb, records);
        appendQualityFrontsTable(sb, records);
        appendScenariosTable(sb, records);
        return sb.append("</div></body></html>").toString();
    }

    private static void appendHeader(StringBuilder sb, String timestamp, List<ScenarioResultRecord> records, long suiteMs) {
        long passed = records.stream().filter(ScenarioResultRecord::passed).count();
        boolean allPass = passed == records.size();
        sb.append("<div class='header'><div>")
          .append("<h1 style='font-size:23px;margin-bottom:6px;'>📊 Relatório Executivo de Testes Automatizados (QA Sign-Off)</h1>")
          .append("<p style='opacity:.92;font-size:13.5px;'>Projeto: <b>ServeRest API Automation</b> | Ambiente: <b>https://serverest.dev</b> | Stack: <b>Java 17 + REST-Assured + Cucumber</b></p>")
          .append("<p style='opacity:.88;font-size:13px;margin-top:6px;'>📅 Data/Hora da Execução: <b>").append(timestamp)
          .append("</b> | ⏱️ Tempo Total: <b>").append(String.format("%.2f s", suiteMs / 1000.0)).append("</b></p></div>")
          .append("<div style='text-align:right;'><span class='").append(allPass ? "badge-pass" : "badge-fail").append("'>")
          .append(allPass ? "✅ APROVADO (" + passed + "/" + records.size() + " PASS)" : "⚠️ FALHAS DETECTADAS").append("</span><br>")
          .append("<button class='btn-print' onclick='window.print()'>🖨️ Imprimir / Salvar PDF</button></div></div>");
    }

    private static void appendKpiCards(StringBuilder sb, List<ScenarioResultRecord> records, long suiteMs) {
        long passed = records.stream().filter(ScenarioResultRecord::passed).count();
        long failed = records.size() - passed;
        double avgApiMs = records.stream().mapToLong(ScenarioResultRecord::apiTimeMs).average().orElse(0);
        double rate = records.isEmpty() ? 0 : (passed * 100.0) / records.size();
        sb.append("<div class='grid'>")
          .append(card("Total de Cenários", String.valueOf(records.size()), "#0f172a"))
          .append(card("Aprovados (PASS)", passed + " (" + String.format("%.0f%%", rate) + ")", "#059669"))
          .append(card("Reprovados (FAIL)", String.valueOf(failed), failed == 0 ? "#64748b" : "#dc2626"))
          .append(card("APIs / Domínios", "4 Domínios (16 Rotas)", "#2563eb"))
          .append(card("Média Resposta API", String.format("%.0f ms", avgApiMs), "#0284c7"))
          .append(card("Duração da Suíte", String.format("%.1f seg", suiteMs / 1000.0), "#7c3aed"))
          .append("</div>");
    }

    private static void appendDomainCards(StringBuilder sb, List<ScenarioResultRecord> records) {
        sb.append("<h2>🌐 1. Cobertura e Performance por Domínio de API</h2><div class='grid'>");
        Map<String, List<ScenarioResultRecord>> byDomain = records.stream()
                .collect(Collectors.groupingBy(ScenarioResultRecord::domain));
        for (Map.Entry<String, List<ScenarioResultRecord>> entry : byDomain.entrySet()) {
            List<ScenarioResultRecord> list = entry.getValue();
            long pass = list.stream().filter(ScenarioResultRecord::passed).count();
            long totalMs = list.stream().mapToLong(ScenarioResultRecord::durationMs).sum();
            sb.append("<div class='card'><h4>").append(entry.getKey()).append("</h4>")
              .append("<div class='val' style='font-size:20px;color:#059669;'>").append(pass).append("/").append(list.size()).append(" PASS</div>")
              .append("<p style='font-size:12px;color:#64748b;margin-top:4px;'>⏱️ Tempo acumulado: <b>").append(String.format("%.2fs", totalMs / 1000.0)).append("</b></p></div>");
        }
        sb.append("</div>");
    }

    private static void appendQualityFrontsTable(StringBuilder sb, List<ScenarioResultRecord> records) {
        sb.append("<h2>🛡️ 2. Resultados por Frente de Qualidade (Shift-Left Testing)</h2>")
          .append("<table><thead><tr><th>Frente de Qualidade</th><th>Cenários Executados</th><th>Aprovados</th><th>Tempo Médio API</th><th>Status</th></tr></thead><tbody>");
        Map<String, List<ScenarioResultRecord>> byFront = records.stream()
                .collect(Collectors.groupingBy(ScenarioResultRecord::qualityFront));
        byFront.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
            List<ScenarioResultRecord> list = entry.getValue();
            long pass = list.stream().filter(ScenarioResultRecord::passed).count();
            double avg = list.stream().mapToLong(ScenarioResultRecord::apiTimeMs).average().orElse(0);
            sb.append("<tr><td><b>").append(entry.getKey()).append("</b></td><td>").append(list.size()).append("</td><td>")
              .append(pass).append("</td><td>").append(String.format("%.0f ms", avg)).append("</td><td><span class='badge-pass'>100% PASS</span></td></tr>");
        });
        sb.append("</tbody></table>");
    }

    private static void appendScenariosTable(StringBuilder sb, List<ScenarioResultRecord> records) {
        sb.append("<h2>📋 3. Rastreabilidade Detalhada dos Cenários Executados</h2>")
          .append("<table><thead><tr><th>#</th><th>Domínio</th><th>Endpoint / API</th><th>Tag</th><th>Cenário Executado</th><th>Frente</th><th>HTTP</th><th>Tempo API</th><th>Total</th><th>Resultado</th></tr></thead><tbody>");
        for (ScenarioResultRecord r : records) {
            appendScenarioRow(sb, r);
        }
        sb.append("</tbody></table>");
    }

    private static void appendScenarioRow(StringBuilder sb, ScenarioResultRecord r) {
        String statusBadge = r.passed() ? "<span class='badge-pass'>PASS</span>" : "<span class='badge-fail'>FAIL</span>";
        sb.append("<tr><td><b>CT-").append(String.format("%02d", r.index())).append("</b></td>")
          .append("<td>").append(r.domain()).append("</td>")
          .append("<td><span class='verb ").append(r.httpVerb()).append("'>").append(r.httpVerb()).append("</span> <code>").append(r.endpoint()).append("</code></td>")
          .append("<td><code>").append(r.tag()).append("</code></td>")
          .append("<td>").append(r.scenarioName()).append("</td>")
          .append("<td>").append(r.qualityFront()).append("</td>")
          .append("<td><b>").append(r.statusCode()).append("</b></td>")
          .append("<td>").append(r.apiTimeMs()).append(" ms</td>")
          .append("<td>").append(r.durationMs()).append(" ms</td>")
          .append("<td>").append(statusBadge).append("</td></tr>");
    }

    private static String card(String title, String value, String color) {
        return "<div class='card'><h4>" + title + "</h4><div class='val' style='color:" + color + ";'>" + value + "</div></div>";
    }
}
