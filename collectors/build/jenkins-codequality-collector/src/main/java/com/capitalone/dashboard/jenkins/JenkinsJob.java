package com.capitalone.dashboard.jenkins;

import com.capitalone.dashboard.model.CollectorItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class JenkinsJob extends CollectorItem {

    private String url;

    private String name;

    private JenkinsBuild lastSuccessfulBuild;

    private List<JenkinsJob> jobs;

    private JenkinsJob() {
        // required for converter
    }

    private JenkinsJob(Builder builder) {
        url = builder.url;
        name = builder.jobName;
        lastSuccessfulBuild = builder.lastSuccessfulBuild;
        jobs = Collections.unmodifiableList(builder.jobs);
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

    public List<JenkinsJob> getJobs() {
        return jobs;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Stream<JenkinsJob> streamJobs(){
        if (null!=jobs) {
            return Stream.concat(Stream.of(this), jobs.stream().flatMap(JenkinsJob::streamJobs));
        }
        return Stream.of(this);
    }

    public static final class Builder{

        private String url;
        private String jobName;
        private JenkinsBuild lastSuccessfulBuild;
        private List<JenkinsJob> jobs = new ArrayList<>();

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

        public Builder job(JenkinsJob job) {
            jobs.add(job);
            return this;
        }
    }

}
