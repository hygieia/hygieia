package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.List;

public class FeatureEpicResult {
    List<Feature> featureList = new ArrayList<>();
    List<Epic> epicList = new ArrayList<>();


    public List<Feature> getFeatureList() {
        return featureList;
    }

    public void setFeatureList(List<Feature> featureList) {
        this.featureList = featureList;
    }

    public List<Epic> getEpicList() {
        return epicList;
    }

    public void setEpicList(List<Epic> epicList) {
        this.epicList = epicList;
    }
}
