package com.capitalone.dashboard.collector;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;


/**
 * Created by stevegal on 07/06/2018.
 */
public class AWSCloudSettingsTest {

    @Test
    public void filterPropertyCannotBeNull(){
        AWSCloudSettings settings = new AWSCloudSettings();

        assertNotNull(settings.getFilters());
    }

}