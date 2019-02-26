package com.capitalone.dashboard.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Detailed record of defects in a coverity scan.
 */
@Document(collection = "coverity_scan")
public class CoverityScan extends BaseModel {

    // project identification
    private String name;
    private long projectKey;
    private String stream;
    private String covConnectUrl;

    // foreign key to CoverityProject
    private ObjectId collectorItemId;

    private long timestamp;

    // keys are Impact level (Low, Medium, High, Critical)
    private Map<String, List<Defect>> defectsBySeverity = new HashMap<>();

    public static class Defect {
        // These fields seem relevant...
        private long cid;
        private String type;
        private String filePath;
        private String cvssSeverity;
        private String cvssScore;

        public Defect(long cid, String type, String filePath, String cvssSeverity, String cvssScore) {
            this.cid = cid;
            this.type = type;
            this.filePath = filePath;
            this.cvssSeverity = cvssSeverity;
            this.cvssScore = cvssScore;
        }

        public long getCid() {
            return cid;
        }

        public String getType() {
            return type;
        }

        public String getFilePath() {
            return filePath;
        }

        public String getCvssSeverity() {
            return cvssSeverity;
        }

        public String getCvssScore() {
            return cvssScore;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(long projectKey) {
        this.projectKey = projectKey;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getCovConnectUrl() {
        return covConnectUrl;
    }

    public void setCovConnectUrl(String covConnectUrl) {
        this.covConnectUrl = covConnectUrl;
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

    public Map<String, List<Defect>> getDefectsBySeverity() {
        return defectsBySeverity;
    }

    public void setDefectsBySeverity(Map<String, List<Defect>> defectsBySeverity) {
        this.defectsBySeverity = defectsBySeverity;
    }
}
