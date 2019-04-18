package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.jenkins.JenkinsJob;
import com.capitalone.dashboard.jenkins.model.JenkinsCodeQualityJob;
import com.capitalone.dashboard.model.quality.QualityVisitee;

import java.util.List;

public interface CodeQualityService {

    boolean storeJob(JenkinsJob job, JenkinsCodeQualityJob codeQualityJob, List<? extends QualityVisitee> report);

}
