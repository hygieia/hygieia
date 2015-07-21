package com.capitalone.dashboard.datafactory.jira.sdk.config;

import com.capitalone.dashboard.datafactory.jira.sdk.util.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Supplier that loads properties from a file specified by the DASHBOARD_PROP environment variable
 * or by the dashboard.prop system property. If neither options are provided, an empty Properties
 * object is returned.
 */
public class ApiPropertiesSupplier implements Supplier<Properties> {
    private static final Log LOGGER = LogFactory.getLog(ApiPropertiesSupplier.class);

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
        FileInputStream fileInputStream = null;

        // First check to see if environment variable exists
        String propertiesFile = System.getenv("JIRA_CLIENT_PROP");
        if (propertiesFile == null) {
            // Is there a system prop?
            propertiesFile = System.getProperty("jira.client.prop");
        }

        if (propertiesFile != null) {
            try {
                fileInputStream = new FileInputStream(propertiesFile);
                properties.load(fileInputStream);

            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            } finally {
                try {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                } catch (IOException e) {
                    LOGGER.error("Exception while closing the FileInputStream :" + e.getMessage());
                }
            }
        }
    }
}
