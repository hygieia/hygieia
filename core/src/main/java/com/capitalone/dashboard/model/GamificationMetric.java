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

    private List<GamificationScoringRange> gamificationScoringRanges;

    public List<GamificationScoringRange> getGamificationScoringRanges() {
        return gamificationScoringRanges;
    }

    public void setGamificationScoringRanges(List<GamificationScoringRange> gamificationScoringRanges) {
        this.gamificationScoringRanges = gamificationScoringRanges;
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

    @Override
    public int hashCode() {
        return this.getId() != null ? this.getId().hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GamificationMetric other = (GamificationMetric) obj;
        return other.metricName.equals(this.metricName) && other.getId().equals(this.getId());
    }
}
