package com.capitalone.dashboard.model;


/**
 * Created by stephengalbraith on 11/10/2016.
 */
public class JenkinsCodeQualityJob extends CollectorItem {

    private static final String JOB_NAME = "jobName";
    private static final String JENKINS_SERVER = "jenkinsServer";

    public JenkinsCodeQualityJob(Builder builder) {
        this.getOptions().put(JOB_NAME, builder.jobName);
        this.getOptions().put(JENKINS_SERVER, builder.jenkinsServer);
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
        String jobName;
        String jenkinsServer;

        public Builder jobName(String jobName) {
            this.jobName = jobName;
            return this;
        }

        public Builder jenkinsServer(String jenkinsServer) {
            this.jenkinsServer = jenkinsServer;
            return this;
        }

        public JenkinsCodeQualityJob build() {
            return new JenkinsCodeQualityJob(this);
        }
    }
}
