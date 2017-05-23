package com.capitalone.dashboard.model;

import org.json.simple.JSONArray;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "maturity_model")
public class MaturityModel {
    @Indexed
    private String profile;
    private JSONArray rules;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public JSONArray getRules() {
        return rules;
    }

    public void setRules(JSONArray rules) {
        this.rules = rules;
    }
}
