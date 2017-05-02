package com.capitalone.dashboard.model;

import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Document(collection = "library_policy")
public class LibraryPolicyResult extends BaseModel {
    private ObjectId collectorItemId;
    private long timestamp;
    private long evaluationTimestamp;
    private Map<LibraryPolicyType, Set<Threat>> threats = new HashMap<>();
    private String reportUrl;


    public static class Threat {
        LibraryPolicyThreatLevel level;
        List<String> components = new ArrayList<>();
        int count;

        public Threat(LibraryPolicyThreatLevel level, int count) {
            this.level = level;
            this.count = count;
        }

        public LibraryPolicyThreatLevel getLevel() {
            return level;
        }

        public void setLevel(LibraryPolicyThreatLevel level) {
            this.level = level;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<String> getComponents() {
            return components;
        }

        public void setComponents(List<String> components) {
            this.components = components;
        }
    }


    public ObjectId getCollectorItemId() {
        return collectorItemId;
    }

    public void setCollectorItemId(ObjectId collectorItemId) {
        this.collectorItemId = collectorItemId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<LibraryPolicyType, Set<Threat>> getThreats() {
        return threats;
    }


    public void addThreat(LibraryPolicyType type, LibraryPolicyThreatLevel level, String component) {
        Set<Threat> threatSet = threats.get(type);

        if (CollectionUtils.isEmpty(threatSet)) {
            Threat threat = new Threat(level, 1);
            threat.getComponents().add(component);
            Set<Threat> set = new HashSet<>();
            set.add(threat);
            threats.put(type, set);
        } else {
            boolean found = false;
            for (Threat t : threatSet) {
                if (t.getLevel().equals(level)) {
                    t.setCount(t.getCount() + 1);
                    if (!t.getComponents().contains(component)) {
                        t.getComponents().add(component);
                    }
                    threatSet.add(t);
                    threats.put(type, threatSet);
                    found = true;
                    break;
                }
            }
            if (!found) {
                Threat t = new Threat(level, 1);
                if (!t.getComponents().contains(component)) {
                    t.getComponents().add(component);
                }
                threatSet.add(t);
                threats.put(type, threatSet);
            }
        }
    }


    public void setThreats(Map<LibraryPolicyType, Set<Threat>> threats) {
        this.threats = threats;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }

    public long getEvaluationTimestamp() {
        return evaluationTimestamp;
    }

    public void setEvaluationTimestamp(long evaluationTimestamp) {
        this.evaluationTimestamp = evaluationTimestamp;
    }
}
