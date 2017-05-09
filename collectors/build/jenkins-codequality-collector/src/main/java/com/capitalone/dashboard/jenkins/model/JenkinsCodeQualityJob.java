package com.capitalone.dashboard.jenkins.model;


import com.capitalone.dashboard.model.CollectorItem;
import org.bson.types.ObjectId;

public class JenkinsCodeQualityJob extends CollectorItem {

    private static final String JOB_NAME = "jobName";
    private static final String JENKINS_SERVER = "jenkinsServer";

    public JenkinsCodeQualityJob(Builder builder) {
        this.getOptions().put(JOB_NAME, builder.jobName);
        this.getOptions().put(JENKINS_SERVER, builder.jenkinsServer);
        this.setCollectorId(builder.collectorId);
        this.setNiceName(builder.jobName);
        this.setDescription(builder.description);
    }

    public String getJobName() {
        return (String) this.getOptions().get(JOB_NAME);
    }

    public String getJenkinsServer() {
        return (String) this.getOptions().get(JENKINS_SERVER);
    }

    public JenkinsCodeQualityJob() {
        // provided in case it's required by mongo etc
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String jobName;
        private String jenkinsServer;
        private String description;
        private ObjectId collectorId;

        public Builder jobName(String jobName) {
            this.jobName = jobName;
            return this;
        }

        public Builder jenkinsServer(String jenkinsServer) {
            this.jenkinsServer = jenkinsServer;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }


        public JenkinsCodeQualityJob build() {
            return new JenkinsCodeQualityJob(this);
        }

        public Builder collectorId(ObjectId collectorId) {
            this.collectorId = collectorId;
            return this;
        }
    }
}
