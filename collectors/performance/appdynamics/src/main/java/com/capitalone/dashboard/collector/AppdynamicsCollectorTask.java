package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.AppDynamicsApplicationRepository;
import com.capitalone.dashboard.repository.AppdynamicsCollectorRepository;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.PerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class AppdynamicsCollectorTask extends CollectorTask<AppdynamicsCollector> {


    private final AppdynamicsCollectorRepository appdynamicsCollectorRepository;
    private final AppDynamicsApplicationRepository appDynamicsApplicationRepository;
    private final PerformanceRepository performanceRepository;
    private final AppdynamicsClient appdynamicsClient;
    private final AppdynamicsSettings appdynamicsSettings;




    @Autowired
    public AppdynamicsCollectorTask(TaskScheduler taskScheduler,
                                    AppdynamicsCollectorRepository appdynamicsCollectorRepository,
                                    AppDynamicsApplicationRepository appDynamicsApplicationRepository,
                                    PerformanceRepository performanceRepository,
                                    AppdynamicsSettings appdynamicsSettings,
                                    AppdynamicsClient appdynamicsClient) {
        super(taskScheduler, "Appdynamics");
        this.appdynamicsCollectorRepository = appdynamicsCollectorRepository;
        this.appDynamicsApplicationRepository = appDynamicsApplicationRepository;
        this.performanceRepository = performanceRepository;
        this.appdynamicsSettings = appdynamicsSettings;
        this.appdynamicsClient = appdynamicsClient;
    }

    @Override
    public AppdynamicsCollector getCollector() {
        return AppdynamicsCollector.prototype(appdynamicsSettings);
    }

    @Override
    public BaseCollectorRepository<AppdynamicsCollector> getCollectorRepository() {
        return appdynamicsCollectorRepository;
    }

    @Override
    public String getCron() {
        return appdynamicsSettings.getCron();
    }

    @Override
    public void collect(AppdynamicsCollector collector) {

        long start = System.currentTimeMillis();
        List<String> instanceURLs = collector.getInstanceUrls();
        List<Set<AppdynamicsApplication>> apps = new ArrayList<>();
        for (int i = 0; i < instanceURLs.size(); i++)
            apps.add(new HashSet<>());

        int index = 0;
        for (String instanceURL : instanceURLs) {

            logBanner("Instance " + index + ": " + instanceURL);

            List<AppdynamicsApplication> existingApps = appDynamicsApplicationRepository.findByCollectorIdAndInstanceID(collector.getId(), index);

            apps.get(index).addAll(appdynamicsClient.getApplications(instanceURL));


            log("Fetched applications   " + ((apps.get(index) != null) ? apps.size() : 0), start);

            addNewProjects(apps.get(index), existingApps, collector, instanceURL, index);

            refreshData(enabledApplications(collector, index), instanceURL);
            index++;
        }

        log("Finished", start);
    }




    private void refreshData(List<AppdynamicsApplication> apps, String instanceURL) {
        long start = System.currentTimeMillis();
        int count = 0;

        for (AppdynamicsApplication app : apps) {
            List<PerformanceMetric> metrics = appdynamicsClient.getPerformanceMetrics(app, instanceURL);

            if (!CollectionUtils.isEmpty(metrics)) {
                Performance performance = new Performance();
                performance.setCollectorItemId(app.getId());
                performance.setTimestamp(System.currentTimeMillis());
                performance.setType(PerformanceType.ApplicationPerformance);
                performance.getInstances().put(replaceDot(instanceURL), new HashSet<>(metrics));
                if (isNewPerformanceData(app, performance)) {
                    performanceRepository.save(performance);
                    count++;
                }
            }
        }
        log("Updated", start, count);
    }



    private static String replaceDot(String suspect){

        if(!suspect.isEmpty()){
           return suspect.replaceAll("\\.","U+00B7");
        }

        return suspect;
    }

//    public static void main(String args[]){
//        String str = replaceDot("http://appdyn-hqa-c01.kdc.capitalone.com");
//        System.out.println(str);
//    }


    private List<AppdynamicsApplication> enabledApplications(AppdynamicsCollector collector, int instanceID) {
//        return appDynamicsApplicationRepository.findEnabledAppdynamicsApplications(collector.getId());
        return appDynamicsApplicationRepository.findByCollectorIdAndEnabledAndInstanceID(collector.getId(), true, instanceID);
    }


    private void addNewProjects(Set<AppdynamicsApplication> allApps, List<AppdynamicsApplication> exisingApps, AppdynamicsCollector collector, String instanceURL, int instanceID) {
        long start = System.currentTimeMillis();
        int count = 0;
        Set<AppdynamicsApplication> newApps = new HashSet<>();

        for (AppdynamicsApplication app : allApps) {
            if (!exisingApps.contains(app)) {
                app.setCollectorId(collector.getId());
                app.setAppDashboardUrl(String.format(appdynamicsSettings.getDashboardUrl(instanceURL),app.getAppID()));
                app.setEnabled(false);
                app.setinstanceID(instanceID);
                //app.getOptions().put("instanceID", app.getinstanceID());
                newApps.add(app);
                count++;
            }


        }
        //save all in one shot
        if (!CollectionUtils.isEmpty(newApps)) {
            appDynamicsApplicationRepository.save(newApps);
        }
        log("New appplications: ", start, count);
    }

    private boolean isNewPerformanceData(AppdynamicsApplication appdynamicsApplication, Performance performance) {
        return performanceRepository.findByCollectorItemIdAndTimestamp(
                appdynamicsApplication.getId(), performance.getTimestamp()) == null;
    }
}
