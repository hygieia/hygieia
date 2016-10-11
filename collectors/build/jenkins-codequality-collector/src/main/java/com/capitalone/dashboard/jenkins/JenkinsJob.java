package com.capitalone.dashboard.jenkins;

import com.capitalone.dashboard.model.CollectorItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephengalbraith on 10/10/2016.
 */
public class JenkinsJob extends CollectorItem {

    private static final String INSTANCE_URL = "jenkinsServer";
    private static final String PROJECT_NAME = "jobName";
    private static final String PROJECT_ID = "projectId";

    private String jenkinsServer;

    private String jobName;
    private List<Artifact> artifacts;

    private JenkinsJob(Builder builder) {
        jenkinsServer = builder.jenkinsServer;
        jobName = builder.jobName;
        artifacts = builder.artifacts;
    }

    public String getJenkinsServer() {
        return jenkinsServer;
    }

    public String getJobName() {
        return jobName;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public List<Artifact> getArtifacts() {
        return artifacts;
    }

    public static final class Builder{

        private String jenkinsServer;
        private String jobName;
        private List<Artifact> artifacts = new ArrayList<>();

        public Builder jenkinsServer(String jenkinsServer) {
            this.jenkinsServer = jenkinsServer;
            return this;
        }

        public Builder jobName(String jobName) {
            this.jobName = jobName;
            return this;
        }

        public JenkinsJob build() {
            return new JenkinsJob(this);
        }

        public Builder artifact(Artifact artifact) {
            this.artifacts.add(artifact);
            return this;
        }
    }

}
