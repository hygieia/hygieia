package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.jenkins.JenkinsJob;
import com.capitalone.dashboard.model.CodeQualityVisitee;
import com.capitalone.dashboard.model.JenkinsCodeQualityJob;

import java.util.List;

/**
 * Created by plv163 on 19/10/2016.
 */
public interface CodeQualityService {

    void storeJob(JenkinsJob job, JenkinsCodeQualityJob codeQualityJob, List<? extends CodeQualityVisitee> report);

}
