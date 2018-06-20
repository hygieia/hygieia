package com.capitalone.dashboard.cloudwatch.model;

import com.capitalone.dashboard.model.CollectorItem;

/**
 * Created by stevegal on 20/06/2018.
 */
public class AwsLogCollectorItem extends CollectorItem {

    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
