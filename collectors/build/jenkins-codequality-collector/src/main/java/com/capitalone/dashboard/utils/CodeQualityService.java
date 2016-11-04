package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.model.CodeQualityVisitee;
import com.capitalone.dashboard.model.JenkinsCodeQualityJob;

import java.util.List;

/**
 * Created by plv163 on 19/10/2016.
 */
public interface CodeQualityService {

    void storeJob(String jobUrl, JenkinsCodeQualityJob job, List<? extends CodeQualityVisitee> report);

}
