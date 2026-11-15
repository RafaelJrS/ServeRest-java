package br.com.serverest.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public final class RegressionReportUpdater {

    private static final Path REPORT_PATH = Path.of("RELATORIO_FINAL_DE_TESTES.md");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm:ss");
    private static final AtomicInteger TOTAL = new AtomicInteger(0);
    private static final AtomicInteger PASSED = new AtomicInteger(0);

    private RegressionReportUpdater() {
    }

    public static void recordScenario(boolean isFailed) {
        TOTAL.incrementAndGet();
        if (!isFailed) {
            PASSED.incrementAndGet();
        }
    }

    public static void updateReportIfFullSuite() {
        String filterTags = System.getProperty("cucumber.filter.tags", "");
        if (!filterTags.isBlank() || !Files.exists(REPORT_PATH) || TOTAL.get() == 0) {
            return;
        }
        writeUpdatedTimestamp();
    }

    private static void writeUpdatedTimestamp() {
        try {
            String content = Files.readString(REPORT_PATH, StandardCharsets.UTF_8);
            String timestamp = LocalDateTime.now().format(FORMATTER);
            String updated = content.replaceFirst(
                    "\\*\\*Última Execução Regressiva Completa:\\*\\* .*",
                    "**Última Execução Regressiva Completa:** `" + timestamp + "` (" + PASSED.get() + "/" + TOTAL.get() + " PASS)  ");
            Files.writeString(REPORT_PATH, updated, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            // Mantém o relatório intacto caso o arquivo esteja bloqueado
        }
    }
}
