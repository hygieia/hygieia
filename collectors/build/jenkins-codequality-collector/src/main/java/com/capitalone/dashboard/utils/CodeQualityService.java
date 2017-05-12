package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.jenkins.JenkinsJob;
import com.capitalone.dashboard.jenkins.model.JenkinsCodeQualityJob;
import com.capitalone.dashboard.model.quality.CodeQualityVisitee;

import java.util.List;

public interface CodeQualityService {

    boolean storeJob(JenkinsJob job, JenkinsCodeQualityJob codeQualityJob, List<? extends CodeQualityVisitee> report);

}
