package br.com.serverest.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigurationManager {

    private static final String CONFIG_FILE = "config/application.properties";
    private static final Properties PROPERTIES = loadProperties();

    private ConfigurationManager() {
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = ConfigurationManager.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Arquivo de configuração não encontrado: " + CONFIG_FILE);
            }
            props.load(input);
            return props;
        } catch (IOException ex) {
            throw new IllegalStateException("Falha ao carregar o arquivo de configuração.", ex);
        }
    }

    public static String getProperty(String key) {
        String envKey = key.toUpperCase().replace('.', '_');
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        return System.getProperty(key, PROPERTIES.getProperty(key));
    }

    public static int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        return (value != null && !value.isBlank()) ? Integer.parseInt(value.trim()) : defaultValue;
    }
}

