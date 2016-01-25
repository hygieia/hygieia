package com.capitalone.dashboard.model;

public class ProductDashboardCollector extends Collector {

    public static ProductDashboardCollector prototype(){

        ProductDashboardCollector prototype = new ProductDashboardCollector();
        prototype.setName("Product");
        prototype.setCollectorType(CollectorType.Product);
        prototype.setEnabled(true);
        prototype.setOnline(true);
        return prototype;
    }
}
