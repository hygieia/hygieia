package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.JenkinsCodeQualityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.client.RestTemplate;

/**
 * Created by stephengalbraith on 10/10/2016.
 */
public class JenkinsCodeQualityCollectorTask extends CollectorTask<JenkinsCodeQualityCollector> {


    private JenkinsCodeQualityRepository repository;
    private String cronSchedule;
    private JenkinsClient jenkinsClient;

    @Autowired
    public JenkinsCodeQualityCollectorTask(TaskScheduler taskScheduler, JenkinsCodeQualityRepository repository, String cronSchedule, JenkinsClient jenkinsClient) {
        super(taskScheduler,"JenkinsCodeQuality");
        this.repository = repository;
        this.cronSchedule = cronSchedule;
        this.jenkinsClient = jenkinsClient;
    }

    public JenkinsCodeQualityCollector getCollector() {
        return new JenkinsCodeQualityCollector();
    }

    @Override
    public JenkinsCodeQualityRepository getCollectorRepository() {
         return this.repository;
    }

    @Override
    public String getCron() {
        return this.cronSchedule;
    }

    @Override
    public void collect(JenkinsCodeQualityCollector collector) {



    }
}
