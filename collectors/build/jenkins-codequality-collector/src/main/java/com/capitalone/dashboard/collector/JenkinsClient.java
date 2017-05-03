package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.jenkins.JenkinsJob;

import java.util.List;
import java.util.regex.Pattern;

public interface JenkinsClient {

    List<JenkinsJob> getJobs(List<String> servers);

    <T> List<T> getLatestArtifacts(Class<T> type, JenkinsJob job, Pattern matchingJobPatterns);

}
