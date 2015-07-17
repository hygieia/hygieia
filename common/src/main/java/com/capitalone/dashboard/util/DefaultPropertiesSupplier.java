package com.capitalone.dashboard.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Supplier that loads properties from a file specified by the DASHBOARD_PROP environment variable
 * or by the dashboard.prop system property. If neither options are provided, an empty Properties
 * object is returned.
 */
@Component
public class DefaultPropertiesSupplier implements Supplier<Properties> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPropertiesSupplier.class);

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
        String propertiesFile = System.getenv("DASHBOARD_PROP");
        if (propertiesFile == null) {
            // Is there a system prop?
            propertiesFile = System.getProperty("dashboard.prop");
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
