package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.jenkins.JenkinsJob;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by stephengalbraith on 10/10/2016.
 */
public interface JenkinsClient {

    List<JenkinsJob> getJobs(Iterable<String> servers);

    <T> List<T> getLatestArtifacts(Class<T> type, JenkinsJob job, List<Pattern> matchingJobPatterns);

}
