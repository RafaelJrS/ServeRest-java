package br.com.serverest.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import net.datafaker.Faker;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class DataPoolReader {

    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final Faker FAKER = new Faker(new Locale("pt", "BR"));

    private DataPoolReader() {
    }

    public static Map<String, Object> getDynamicPayload(String dataPoolFile, String datasetKey) {
        Map<String, Map<String, Object>> rootData = readYamlDataPool(dataPoolFile);
        Map<String, Object> rawTemplate = rootData.get(datasetKey);
        if (rawTemplate == null) {
            throw new IllegalArgumentException("Chave '" + datasetKey + "' não encontrada em: " + dataPoolFile);
        }
        return resolvePlaceholders(rawTemplate);
    }

    public static String toJsonString(Map<String, Object> payloadMap) {
        try {
            return JSON_MAPPER.writeValueAsString(payloadMap);
        } catch (IOException ex) {
            throw new IllegalStateException("Erro ao serializar payload dinâmico para JSON.", ex);
        }
    }

    private static Map<String, Map<String, Object>> readYamlDataPool(String dataPoolFile) {
        String resourcePath = "datapool/" + dataPoolFile;
        try (InputStream is = DataPoolReader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Arquivo de DataPool não encontrado: " + resourcePath);
            }
            return YAML_MAPPER.readValue(is, new TypeReference<>() {});
        } catch (IOException ex) {
            throw new IllegalStateException("Falha ao ler o arquivo YAML: " + resourcePath, ex);
        }
    }

    private static Map<String, Object> resolvePlaceholders(Map<String, Object> template) {
        Map<String, Object> resolved = new LinkedHashMap<>();
        template.forEach((key, value) -> resolved.put(key, interpolateValue(value)));
        return resolved;
    }

    private static Object interpolateValue(Object value) {
        if (!(value instanceof String text)) {
            return value;
        }
        return switch (text) {
            case "{{DYNAMIC_NAME}}" -> FAKER.name().fullName();
            case "{{DYNAMIC_EMAIL}}" -> "qa." + UUID.randomUUID().toString().substring(0, 8) + "@serverest.dev";
            case "{{DYNAMIC_PASSWORD}}" -> FAKER.internet().password(8, 14, true, true);
            case "{{DYNAMIC_PRODUCT}}" -> FAKER.commerce().productName() + " " + UUID.randomUUID().toString().substring(0, 5);
            default -> text;
        };
    }
}

