package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CodeQualityType;
import com.capitalone.dashboard.model.CodeQualityVisitee;
import com.capitalone.dashboard.model.JenkinsCodeQualityJob;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by plv163 on 19/10/2016.
 */
@Component
public class CodeQualityDataService implements CodeQualityService {


    private CodeQualityRepository codeQualityRepository;
    private CodeQualityConverter codeQualityConverter;

    @Autowired
    public CodeQualityDataService(CodeQualityRepository codeQualityRepository, CodeQualityConverter codeQualityConverter) {

        this.codeQualityRepository = codeQualityRepository;
        this.codeQualityConverter = codeQualityConverter;
    }


    @Override
    public void storeJob(String jobName, JenkinsCodeQualityJob job, List<? extends CodeQualityVisitee> xmlReportList) {

        // not quite how it works. This should collect all the jobs together to form static analysis and unit test
        // results into one thing. The Functional test are collected in the jenkins-cucumber-test-collector (json output)

        if (null != job && null != xmlReportList && !xmlReportList.isEmpty()) {
            CodeQuality currentJobQuality = computeMetricsForJob(xmlReportList);

            currentJobQuality.setCollectorItemId(job.getId());
            currentJobQuality.setType(CodeQualityType.StaticAnalysis);
            currentJobQuality.setUrl(job.getJenkinsServer());
            currentJobQuality.setName(jobName);

            // store the data only if it doesn't already exist
            if (null == this.codeQualityRepository.findByCollectorItemIdAndTimestamp(job.getId(), currentJobQuality.getTimestamp())) {
                codeQualityRepository.save(currentJobQuality);
            }
        }
    }

    private CodeQuality computeMetricsForJob(List<? extends CodeQualityVisitee> reportArtifacts) {

        CodeQualityVisitor visitor = this.codeQualityConverter.produceVisitor();
        for (CodeQualityVisitee reportArtifact : reportArtifacts) {
            reportArtifact.accept(visitor);
        }
        return visitor.produceResult();
    }
}
