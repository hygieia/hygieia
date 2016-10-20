package com.capitalone.dashboard.utils;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CodeQualityRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by plv163 on 19/10/2016.
 */
public class CodeQualityDataService implements CodeQualityService {


    private CodeQualityRepository codeQualityRepository;
    private CodeQualityConverter codeQualityConverter;

    public CodeQualityDataService(CodeQualityRepository codeQualityRepository, CodeQualityConverter codeQualityConverter) {

        this.codeQualityRepository = codeQualityRepository;
        this.codeQualityConverter = codeQualityConverter;
    }


    @Override
    public void storeJob(String jobName, JenkinsCodeQualityJob job, List<JunitXmlReport> xmlReportList) {

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

    private CodeQuality computeMetricsForJob(List<JunitXmlReport> reportArtifacts) {
        CodeQuality qualityForJob = new CodeQuality();
        Map<String, CodeQualityMetric> currentMetrics = new HashMap<>();
        long timestamp = Long.MIN_VALUE;
        for (JunitXmlReport reportArtifact : reportArtifacts) {
            timestamp = Math.max(timestamp, reportArtifact.getTimestamp().toGregorianCalendar().getTimeInMillis());
            Set<CodeQualityMetric> codeQualityMetrics = this.codeQualityConverter.analyse(reportArtifact);
            Map<String, CodeQualityMetric> reportMetricsMap = codeQualityMetrics.stream().collect(Collectors.toMap(CodeQualityMetric::getName, Function.identity()));

            // for all the metrics we have, combine and add where necessary
            reportMetricsMap.forEach((key, value) -> {
                CodeQualityMetric currentValue = currentMetrics.get(key);
                CodeQualityMetric newValue;
                if (null == currentValue) {
                    newValue = value;
                } else {
                    // do the sum
                    newValue = new CodeQualityMetric(key);
                    newValue.setValue((int) currentValue.getValue() + (int) value.getValue());
                    newValue.setFormattedValue(String.valueOf((int) currentValue.getValue() + (int) value.getValue()));
                    int newOrdinal = Math.max(value.getStatus().ordinal(), currentValue.getStatus().ordinal());
                    newValue.setStatus(CodeQualityMetricStatus.values()[newOrdinal]);
                    String concatMessage = concatStrings(currentValue.getStatusMessage(), value.getStatusMessage());
                    newValue.setStatusMessage(concatMessage);
                }
                currentMetrics.put(key, newValue);
            });

        }
        currentMetrics.forEach((key, value) -> {
            qualityForJob.addMetric(value);
        });

        qualityForJob.setTimestamp(timestamp);
        return qualityForJob;
    }

    private String concatStrings(String statusMessage, String endMessage) {
        String result = null;
        if (statusMessage != null && !statusMessage.isEmpty()) {
            result = statusMessage;
        }
        if (endMessage != null && !endMessage.isEmpty()) {
            result = result != null ? result + "," + endMessage : endMessage;
        }
        return result;
    }
}
