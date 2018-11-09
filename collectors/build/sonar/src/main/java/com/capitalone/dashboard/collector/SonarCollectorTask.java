package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorItemConfigHistory;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.ConfigHistOperationType;
import com.capitalone.dashboard.model.SonarCollector;
import com.capitalone.dashboard.model.SonarProject;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.SonarCollectorRepository;
import com.capitalone.dashboard.repository.SonarProfileRepostory;
import com.capitalone.dashboard.repository.SonarProjectRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class SonarCollectorTask extends CollectorTask<SonarCollector> {
    @SuppressWarnings({ "PMD.UnusedPrivateField", "unused" })
    private static final Log LOG = LogFactory.getLog(SonarCollectorTask.class);

    private final SonarCollectorRepository sonarCollectorRepository;
    private final SonarProjectRepository sonarProjectRepository;
    private final CodeQualityRepository codeQualityRepository;
    private final SonarProfileRepostory sonarProfileRepostory;
    private final SonarClientSelector sonarClientSelector;
    private final SonarSettings sonarSettings;
    private final ComponentRepository dbComponentRepository;

    @Autowired
    public SonarCollectorTask(TaskScheduler taskScheduler,
                              SonarCollectorRepository sonarCollectorRepository,
                              SonarProjectRepository sonarProjectRepository,
                              CodeQualityRepository codeQualityRepository,
                              SonarProfileRepostory sonarProfileRepostory,
                              SonarSettings sonarSettings,
                              SonarClientSelector sonarClientSelector,
                              ComponentRepository dbComponentRepository) {
        super(taskScheduler, "Sonar");
        this.sonarCollectorRepository = sonarCollectorRepository;
        this.sonarProjectRepository = sonarProjectRepository;
        this.codeQualityRepository = codeQualityRepository;
        this.sonarProfileRepostory = sonarProfileRepostory;
        this.sonarSettings = sonarSettings;
        this.sonarClientSelector = sonarClientSelector;
        this.dbComponentRepository = dbComponentRepository;
    }

    @Override
    public SonarCollector getCollector() {
        return SonarCollector.prototype(sonarSettings.getServers(), sonarSettings.getVersions(), sonarSettings.getMetrics(),sonarSettings.getNiceNames());
    }

    @Override
    public BaseCollectorRepository<SonarCollector> getCollectorRepository() {
        return sonarCollectorRepository;
    }

    @Override
    public String getCron() {
        return sonarSettings.getCron();
    }

    @Override
    public void collect(SonarCollector collector) {
        long start = System.currentTimeMillis();

        Set<ObjectId> udId = new HashSet<>();
        udId.add(collector.getId());
        List<SonarProject> existingProjects = sonarProjectRepository.findByCollectorIdIn(udId);
        List<SonarProject> latestProjects = new ArrayList<>();
        clean(collector, existingProjects);

        if (!CollectionUtils.isEmpty(collector.getSonarServers())) {
            
            for (int i = 0; i < collector.getSonarServers().size(); i++) {

                String instanceUrl = collector.getSonarServers().get(i);
                Double version = collector.getSonarVersions().get(i);
                String metrics = collector.getSonarMetrics().get(i);

                logBanner(instanceUrl);
                SonarClient sonarClient = sonarClientSelector.getSonarClient(version);
                List<SonarProject> projects = sonarClient.getProjects(instanceUrl);
                latestProjects.addAll(projects);

                int projSize = ((CollectionUtils.isEmpty(projects)) ? 0 : projects.size());
                log("Fetched projects   " + projSize, start);

                addNewProjects(projects, existingProjects, collector);

                refreshData(enabledProjects(collector, instanceUrl), sonarClient,metrics);
                
                // Changelog apis do not exist for sonarqube versions under version 5.0
                if (version >= 5.0) {
                  try {
                     fetchQualityProfileConfigChanges(collector,instanceUrl,sonarClient);
                   } catch (Exception e) {
                     LOG.error(e);
                    }
                }

                log("Finished", start);
            }
        }
        deleteUnwantedJobs(latestProjects, existingProjects, collector);
    }

	/**
	 * Clean up unused sonar collector items
	 *
	 * @param collector
	 *            the {@link SonarCollector}
	 */

    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts") // agreed PMD, fixme
    private void clean(SonarCollector collector, List<SonarProject> existingProjects) {
        Set<ObjectId> uniqueIDs = new HashSet<>();
        for (com.capitalone.dashboard.model.Component comp : dbComponentRepository
                .findAll()) {
            if (comp.getCollectorItems() != null && !comp.getCollectorItems().isEmpty()) {
                List<CollectorItem> itemList = comp.getCollectorItems().get(
                        CollectorType.CodeQuality);
                if (itemList != null) {
                    for (CollectorItem ci : itemList) {
                        if (ci != null && ci.getCollectorId().equals(collector.getId())) {
                            uniqueIDs.add(ci.getId());
                        }
                    }
                }
            }
        }
        List<SonarProject> stateChangeJobList = new ArrayList<>();
        Set<ObjectId> udId = new HashSet<>();
        udId.add(collector.getId());
        for (SonarProject job : existingProjects) {
            // collect the jobs that need to change state : enabled vs disabled.
            if ((job.isEnabled() && !uniqueIDs.contains(job.getId())) ||  // if it was enabled but not on a dashboard
                    (!job.isEnabled() && uniqueIDs.contains(job.getId()))) { // OR it was disabled and now on a dashboard
                job.setEnabled(uniqueIDs.contains(job.getId()));
                stateChangeJobList.add(job);
            }
        }
        if (!CollectionUtils.isEmpty(stateChangeJobList)) {
            sonarProjectRepository.save(stateChangeJobList);
        }
    }


    private void deleteUnwantedJobs(List<SonarProject> latestProjects, List<SonarProject> existingProjects, SonarCollector collector) {
        List<SonarProject> deleteJobList = new ArrayList<>();

        // First delete collector items that are not supposed to be collected anymore because the servers have moved(?)
        for (SonarProject job : existingProjects) {
            if (job.isPushed()) continue; // do not delete jobs that are being pushed via API
            if (!collector.getSonarServers().contains(job.getInstanceUrl()) ||
                    (!job.getCollectorId().equals(collector.getId())) ||
                    (!latestProjects.contains(job))) {
                deleteJobList.add(job);
            }
        }
        if (!CollectionUtils.isEmpty(deleteJobList)) {
            sonarProjectRepository.delete(deleteJobList);
        }
    }

    private void refreshData(List<SonarProject> sonarProjects, SonarClient sonarClient, String metrics) {
        long start = System.currentTimeMillis();
        int count = 0;

        for (SonarProject project : sonarProjects) {
            CodeQuality codeQuality = sonarClient.currentCodeQuality(project, metrics);
            if (codeQuality != null && isNewQualityData(project, codeQuality)) {
                project.setLastUpdated(System.currentTimeMillis());
                sonarProjectRepository.save(project);
                codeQuality.setCollectorItemId(project.getId());
                codeQualityRepository.save(codeQuality);
                count++;
            }
        }
        log("Updated", start, count);
    }
    
    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
    private void fetchQualityProfileConfigChanges(SonarCollector collector,String instanceUrl,SonarClient sonarClient) throws org.json.simple.parser.ParseException{
    	JSONArray qualityProfiles = sonarClient.getQualityProfiles(instanceUrl);   
    	JSONArray sonarProfileConfigurationChanges = new JSONArray();
        
    	for (Object qualityProfile : qualityProfiles ) {      	
    		JSONObject qualityProfileJson = (JSONObject) qualityProfile;
    		String qualityProfileKey = (String)qualityProfileJson.get("key");

    		List<String> sonarProjects = sonarClient.retrieveProfileAndProjectAssociation(instanceUrl,qualityProfileKey);
    		if (sonarProjects != null){
    			sonarProfileConfigurationChanges = sonarClient.getQualityProfileConfigurationChanges(instanceUrl,qualityProfileKey);
    			addNewConfigurationChanges(collector,sonarProfileConfigurationChanges);
    		}
    	}
    }
    
    private void addNewConfigurationChanges(SonarCollector collector,JSONArray sonarProfileConfigurationChanges){
    	ArrayList<CollectorItemConfigHistory> profileConfigChanges = new ArrayList();
    	
    	for (Object configChange : sonarProfileConfigurationChanges) {		
    		JSONObject configChangeJson = (JSONObject) configChange;
    		CollectorItemConfigHistory profileConfigChange = new CollectorItemConfigHistory();
    		Map<String,Object> changeMap = new HashMap<String,Object>();
    		
    		profileConfigChange.setCollectorItemId(collector.getId());
    		profileConfigChange.setUserName((String) configChangeJson.get("authorName"));
    		profileConfigChange.setUserID((String) configChangeJson.get("authorLogin") );
    		changeMap.put("event", configChangeJson);
   
    		profileConfigChange.setChangeMap(changeMap);
    		
    		ConfigHistOperationType operation = determineConfigChangeOperationType((String)configChangeJson.get("action"));
    		profileConfigChange.setOperation(operation);
    		
				
    		long timestamp = convertToTimestamp((String) configChangeJson.get("date"));
    		profileConfigChange.setTimestamp(timestamp);
    		
    		if (isNewConfig(collector.getId(),(String) configChangeJson.get("authorLogin"),operation,timestamp)) {
    			profileConfigChanges.add(profileConfigChange);
    		}
    	}
    	sonarProfileRepostory.save(profileConfigChanges);
    }
    
    private Boolean isNewConfig(ObjectId collectorId,String authorLogin,ConfigHistOperationType operation,long timestamp) {
    	List<CollectorItemConfigHistory> storedConfigs = sonarProfileRepostory.findProfileConfigChanges(collectorId, authorLogin,operation,timestamp);
    	return storedConfigs.isEmpty();
    }
    
    private List<SonarProject> enabledProjects(SonarCollector collector, String instanceUrl) {
        return sonarProjectRepository.findEnabledProjects(collector.getId(), instanceUrl);
    }

    private void addNewProjects(List<SonarProject> projects, List<SonarProject> existingProjects, SonarCollector collector) {
        long start = System.currentTimeMillis();
        int count = 0;
        List<SonarProject> newProjects = new ArrayList<>();
        List<SonarProject> updateProjects = new ArrayList<>();
        for (SonarProject project : projects) {
            String niceName = getNiceName(project,collector);
            if (!existingProjects.contains(project)) {
                project.setCollectorId(collector.getId());
                project.setEnabled(false);
                project.setDescription(project.getProjectName());
                project.setNiceName(niceName);
                newProjects.add(project);
                count++;
            }else{
                int index = existingProjects.indexOf(project);
                SonarProject s = existingProjects.get(index);
                if(StringUtils.isEmpty(s.getNiceName())){
                    s.setNiceName(niceName);
                    updateProjects.add(s);
                }
            }
        }
        //save all in one shot
        if (!CollectionUtils.isEmpty(newProjects)) {
            sonarProjectRepository.save(newProjects);
        }
        if (!CollectionUtils.isEmpty(updateProjects)) {
            sonarProjectRepository.save(updateProjects);
        }
        log("New projects", start, count);
    }

    private String getNiceName(SonarProject project, SonarCollector sonarCollector){

        if (org.springframework.util.CollectionUtils.isEmpty(sonarCollector.getSonarServers())) return "";
        List<String> servers = sonarCollector.getSonarServers();
        List<String> niceNames = sonarCollector.getNiceNames();
        if (org.springframework.util.CollectionUtils.isEmpty(niceNames)) return "";
        for (int i = 0; i < servers.size(); i++) {
            if (servers.get(i).equalsIgnoreCase(project.getInstanceUrl()) && (niceNames.size() > i)) {
                return niceNames.get(i);
            }
        }
        return "";

    }

    @SuppressWarnings("unused")
	private boolean isNewProject(SonarCollector collector, SonarProject application) {
        return sonarProjectRepository.findSonarProject(
                collector.getId(), application.getInstanceUrl(), application.getProjectId()) == null;
    }

    private boolean isNewQualityData(SonarProject project, CodeQuality codeQuality) {
        return codeQualityRepository.findByCollectorItemIdAndTimestamp(
                project.getId(), codeQuality.getTimestamp()) == null;
    }
    
    private long convertToTimestamp(String date) {
    	
    	DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");
    	DateTime dt = formatter.parseDateTime(date);
    	long d = new DateTime(dt).getMillis();
    	
    	return d;	
    }
    
    private ConfigHistOperationType determineConfigChangeOperationType(String changeAction){
    	switch (changeAction) {
		
	    	case "DEACTIVATED":
	    		return ConfigHistOperationType.DELETED;
	    		
	    	case "ACTIVATED":
	    		return ConfigHistOperationType.CREATED;
	    	default:
	    		return ConfigHistOperationType.CHANGED;
    	}	
    }

}
