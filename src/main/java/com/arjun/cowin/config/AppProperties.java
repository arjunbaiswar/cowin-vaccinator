package com.arjun.cowin.config;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum AppProperties {

    INSTANCE;

    private CompositeConfiguration compositeConfiguration;
    private Logger logger = LoggerFactory.getLogger(AppProperties.class);

    AppProperties() {
        init();
    }

    private void init() {
        try {
            compositeConfiguration = new CompositeConfiguration();
            compositeConfiguration.addConfiguration(new SystemConfiguration());
            compositeConfiguration.addConfiguration(new PropertiesConfiguration("application.properties"));
        } catch (Exception ex) {
            logger.error("Error while loading properties", ex);
        }
    }


    public String getString(String key) {
        return this.compositeConfiguration.getString(key);
    }

    public boolean getBoolean(String key) {
        return this.compositeConfiguration.getBoolean(key);
    }

    public String[] getStringArray(String key) {
        return this.compositeConfiguration.getStringArray(key);
    }
}
