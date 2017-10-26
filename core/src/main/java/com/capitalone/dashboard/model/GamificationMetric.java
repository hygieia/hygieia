package com.capitalone.dashboard.model;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;

@Document(collection = "gamification_metrics")
public class GamificationMetric extends BaseModel {

    @NotNull
    private String metricName;
    private String formattedName;
    private String symbol;
    private String description;
    private Boolean enabled;

    private List<GamificationRangeScore> gamificationRangeScores;

    public List<GamificationRangeScore> getGamificationRangeScores() {
        return gamificationRangeScores;
    }

    public void setGamificationRangeScores(List<GamificationRangeScore> gamificationRangeScores) {
        this.gamificationRangeScores = gamificationRangeScores;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getFormattedName() {
        return formattedName;
    }

    public void setFormattedName(String formattedName) {
        this.formattedName = formattedName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
