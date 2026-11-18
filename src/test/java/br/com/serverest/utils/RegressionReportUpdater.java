package br.com.serverest.utils;

import br.com.serverest.context.TestContext;
import io.cucumber.java.Scenario;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public final class RegressionReportUpdater {

    private static final Path MD_REPORT = Path.of("RELATORIO_FINAL_DE_TESTES.md");
    private static final Path HTML_REPORT = Path.of("relatorios", "RELATORIO_EXECUTIVO_QA.html");
    private static final Path PDF_REPORT = Path.of("relatorios", "RELATORIO_EXECUTIVO_QA.pdf");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm:ss");
    private static final List<ScenarioResultRecord> RECORDS = new CopyOnWriteArrayList<>();
    private static final long SUITE_START_MS = System.currentTimeMillis();

    private RegressionReportUpdater() {
    }

    public static void recordScenario(Scenario scenario, TestContext context, long durationMs) {
        int index = RECORDS.size() + 1;
        RECORDS.add(ScenarioResultRecord.fromScenario(index, scenario, context, durationMs));
    }

    public static void updateReportIfFullSuite() {
        String filterTags = System.getProperty("cucumber.filter.tags", "");
        if (!filterTags.isBlank() || RECORDS.isEmpty()) {
            return;
        }
        String timestamp = LocalDateTime.now().format(FORMATTER);
        long suiteDurationMs = System.currentTimeMillis() - SUITE_START_MS;
        updateMarkdownTimestamp(timestamp);
        generateHtmlAndPdfReports(timestamp, suiteDurationMs);
    }

    private static void updateMarkdownTimestamp(String timestamp) {
        try {
            if (!Files.exists(MD_REPORT)) return;
            long passed = RECORDS.stream().filter(ScenarioResultRecord::passed).count();
            String content = Files.readString(MD_REPORT, StandardCharsets.UTF_8);
            String updated = content.replaceFirst(
                    "\\*\\*Última Execução Regressiva Completa:\\*\\* .*",
                    "**Última Execução Regressiva Completa:** `" + timestamp + "` (" + passed + "/" + RECORDS.size() + " PASS)  ");
            Files.writeString(MD_REPORT, updated, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            // Mantém o arquivo intacto em caso de bloqueio de IO
        }
    }

    private static void generateHtmlAndPdfReports(String timestamp, long suiteDurationMs) {
        try {
            Files.createDirectories(HTML_REPORT.getParent());
            String html = ExecutiveHtmlReportBuilder.buildHtml(RECORDS, timestamp, suiteDurationMs);
            Files.writeString(HTML_REPORT, html, StandardCharsets.UTF_8);
            exportPdfViaHeadlessBrowser();
        } catch (IOException ignored) {
            // Ignora falha de IO sem quebrar a build de testes
        }
    }

    private static void exportPdfViaHeadlessBrowser() {
        Path browser = resolveBrowserExecutable();
        if (browser == null) return;
        try {
            String pdfArg = "--print-to-pdf=" + PDF_REPORT.toAbsolutePath();
            String htmlUri = HTML_REPORT.toAbsolutePath().toUri().toString();
            Process process = new ProcessBuilder(
                    browser.toString(), "--headless", "--disable-gpu", "--no-pdf-header-footer", pdfArg, htmlUri)
                    .redirectErrorStream(true).start();
            process.waitFor(20, TimeUnit.SECONDS);
        } catch (Exception ignored) {
            // Se o navegador não puder renderizar, o HTML permanece disponível
        }
    }

    private static Path resolveBrowserExecutable() {
        List<Path> candidates = List.of(
                Path.of("C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe"),
                Path.of("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe"),
                Path.of("/usr/bin/google-chrome"),
                Path.of("/usr/bin/chromium-browser"));
        return candidates.stream().filter(Files::exists).findFirst().orElse(null);
    }
}
