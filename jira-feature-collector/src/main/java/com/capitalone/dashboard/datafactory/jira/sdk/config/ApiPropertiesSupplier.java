package com.capitalone.dashboard.datafactory.jira.sdk.config;

import com.capitalone.dashboard.datafactory.jira.sdk.util.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Supplier that loads properties from a file specified by the DASHBOARD_PROP environment variable
 * or by the dashboard.prop system property. If neither options are provided, an empty Properties
 * object is returned.
 */
public class ApiPropertiesSupplier implements Supplier<Properties> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiPropertiesSupplier.class);
    private static final String JIRA_CLIENT_ENV = "JIRA_CLIENT_PROP";
    private static final String JIRA_CLIENT_PROP = "jira.client.prop";

    private Properties properties;

    @Override
    public Properties get() {
        if (properties == null) {
            loadProperties();
        }

        return properties;
    }

    private void loadProperties() {
        properties = new Properties();

        // First check to see if environment variable exists
        String propertiesFile = System.getenv(JIRA_CLIENT_ENV);
        if (propertiesFile == null) { // Is there a system prop?
            propertiesFile = System.getProperty(JIRA_CLIENT_PROP);
        }

        if (propertiesFile != null) {
            try (FileInputStream fileInputStream = new FileInputStream(propertiesFile)) {
                properties.load(fileInputStream);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }
}
