package com.capitalone.dashboard.jenkins;

import com.capitalone.dashboard.model.CollectorItem;

/**
 * Created by stephengalbraith on 10/10/2016.
 */
public class JenkinsJob extends CollectorItem {

    private String url;

    private String name;

    private JenkinsBuild lastSuccessfulBuild;

    private JenkinsJob() {
        // required for converter
    }

    private JenkinsJob(Builder builder) {
        url = builder.url;
        name = builder.jobName;
        lastSuccessfulBuild = builder.lastSuccessfulBuild;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public JenkinsBuild getLastSuccessfulBuild() {
        return lastSuccessfulBuild;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder{

        private String url;
        private String jobName;
        private JenkinsBuild lastSuccessfulBuild;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder jobName(String jobName) {
            this.jobName = jobName;
            return this;
        }

        @SuppressWarnings("PMD.AccessorClassGeneration")
        public JenkinsJob build() {
            return new JenkinsJob(this);
        }

        public Builder lastSuccessfulBuild(JenkinsBuild lastSuccessfulBuild) {
            this.lastSuccessfulBuild = lastSuccessfulBuild;
            return this;
        }
    }

}
