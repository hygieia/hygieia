package com.capitalone.dashboard.jenkins;

import org.bson.codecs.DecoderContext;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by stephengalbraith on 10/10/2016.
 */
public class JenkinsJob {

    private String jenkinsServer;

    private String jobName;
    private List<Artefact> artefacts;

    private JenkinsJob(Builder builder) {
        jenkinsServer = builder.jenkinsServer;
        jobName = builder.jobName;
        artefacts=builder.artefacts;
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

    public List<Artefact> getArtefacts() {
        return artefacts;
    }

    public static final class Builder{

        private String jenkinsServer;
        private String jobName;
        private List<Artefact> artefacts = new ArrayList<>();

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

        public Builder artefact(Artefact artefact) {
            this.artefacts.add(artefact);
            return this;
        }
    }

}
