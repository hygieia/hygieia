package com.capitalone.dashboard.jenkins;

import java.util.ArrayList;
import java.util.List;

public class JobContainer {

    private List<JenkinsJob> jobs;

    private JobContainer(Builder builder) {
        jobs = builder.jobs;
    }

    public List<JenkinsJob> getJobs() {
        return jobs;
    }

    @SuppressWarnings("PMD.AccessorClassGeneration")
    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private List<JenkinsJob> jobs = new ArrayList<>();

        private Builder() {
        }

        public Builder job(JenkinsJob job) {
            this.jobs.add(job);
            return this;
        }

        @SuppressWarnings("PMD.AccessorClassGeneration")
        public JobContainer build() {
            return new JobContainer(this);
        }
    }
}
