package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.jenkins.JenkinsJob;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by stephengalbraith on 10/10/2016.
 */
public interface JenkinsClient {

    List<JenkinsJob> getJobs(Iterable<String> servers);
}
