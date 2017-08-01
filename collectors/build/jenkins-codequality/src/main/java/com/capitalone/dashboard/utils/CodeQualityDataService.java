package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.jenkins.JenkinsJob;
import com.capitalone.dashboard.jenkins.model.JenkinsCodeQualityJob;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CodeQualityType;
import com.capitalone.dashboard.model.quality.CodeQualityVisitee;
import com.capitalone.dashboard.model.quality.CodeQualityVisitor;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CodeQualityDataService implements CodeQualityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeQualityDataService.class);


    private CodeQualityRepository codeQualityRepository;
    private CodeQualityConverter codeQualityConverter;

    @Autowired
    public CodeQualityDataService(CodeQualityRepository codeQualityRepository, CodeQualityConverter codeQualityConverter) {

        this.codeQualityRepository = codeQualityRepository;
        this.codeQualityConverter = codeQualityConverter;
    }


    @Override
    public boolean storeJob(JenkinsJob jobName, JenkinsCodeQualityJob job, List<? extends CodeQualityVisitee> xmlReportList) {

        boolean stored=false;
        // not quite how it works. This should collect all the jobs together to form static analysis and unit test
        // results into one thing. The Functional test are collected in the jenkins-cucumber-test-collector (json output)

        if (null != job && null != xmlReportList && !xmlReportList.isEmpty()) {
            CodeQuality currentJobQuality = computeMetricsForJob(xmlReportList);

            currentJobQuality.setTimestamp(jobName.getLastSuccessfulBuild().getTimestamp());
            currentJobQuality.setCollectorItemId(job.getId());
            currentJobQuality.setType(CodeQualityType.StaticAnalysis);
            currentJobQuality.setUrl(job.getJenkinsServer());
            currentJobQuality.setName(jobName.getName());

            // store the data only if it doesn't already exist
            if (null == this.codeQualityRepository.findByCollectorItemIdAndTimestamp(job.getId(), currentJobQuality.getTimestamp())) {
                LOGGER.info("storing new job at timestamp ", currentJobQuality.getTimestamp());
                codeQualityRepository.save(currentJobQuality);
                stored=true;
            }
        }
        return stored;
    }

    private CodeQuality computeMetricsForJob(List<? extends CodeQualityVisitee> reportArtifacts) {

        CodeQualityVisitor visitor = this.codeQualityConverter.produceVisitor();
        for (CodeQualityVisitee reportArtifact : reportArtifacts) {
            reportArtifact.accept(visitor);
        }
        return visitor.produceResult();
    }
}
