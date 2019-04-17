package com.capitalone.dashboard.collector;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Feature;


import com.capitalone.dashboard.model.FeatureEpicResult;

import com.capitalone.dashboard.model.PivotalTrackerCollector;
import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.model.Team;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.PivotalTrackerCollectorRepository;
import com.capitalone.dashboard.repository.ScopeRepository;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
@Component
public class PivotalTrackerCollectorTask extends CollectorTask<PivotalTrackerCollector> {
	//private final FeatureRepository featureRepository;
	 private final PivotalTrackerCollectorRepository pivotalTrackerCollectorRepository;
	 //private final FeatureRepository featureRepository;
	    //private final FeatureBoardRepository featureBoardRepository;
	   // private final TeamRepository teamRepository;
	    private final ScopeRepository projectRepository;
	    //private final FeatureCollectorRepository featureCollectorRepository;
	    private  PivotalTrackerApi pivotalclient;
	    private static final Logger LOGGER = LoggerFactory.getLogger(PivotalTrackerCollectorTask.class);
	    //private final FeatureSettings featureSettings;
	    //private final JiraClient jiraClient;
	 @Value("${cron:0/1 * * * * *}") // Injected from application.properties
	 private String cron;
	 //@Value("${apiToken:2529a7eb5a70fb6f9a1abafbeb822963}") // Injected from application.properties
    // private String apiToken;
	
    
	 
	
	
 @Autowired
 public PivotalTrackerCollectorTask(TaskScheduler taskScheduler, ScopeRepository projectRepository,
             PivotalTrackerCollectorRepository pivotalTrackerCollectorRepository,PivotalTrackerApi pivotalclient) {
         super(taskScheduler, "Pivotal Tracker");
        // this.featureRepository = featureRepository;
         this.pivotalTrackerCollectorRepository = pivotalTrackerCollectorRepository;
         //this.featureCollectorRepository = featureCollectorRepository;
        // this.teamRepository = teamRepository;
         this.projectRepository = projectRepository;
         //this.featureRepository = featureRepository;
         //this.featureSettings = featureSettings;
         //this.jiraClient = jiraClient;
        // this.featureBoardRepository = featureBoardRepository;
         this.pivotalclient=pivotalclient;
     }
     
	@Override
     public PivotalTrackerCollector getCollector() {

         PivotalTrackerCollector collector = new PivotalTrackerCollector();

         collector.setName("Pivotal Tracker"); // Must be unique to all collectors for a given Dashboard Application instance
         collector.setCollectorType(CollectorType.AgileTool);
         collector.setOnline(true);
         collector.setEnabled(true);
         collector.setApiToken("2529a7eb5a70fb6f9a1abafbeb822963");

         return collector;
     }
     
     @Override
     public BaseCollectorRepository<PivotalTrackerCollector> getCollectorRepository() {
         return pivotalTrackerCollectorRepository;
     }
     @Override
 	public String getCron() {
 		// TODO Auto-generated method stub
 		return cron;
 	}
	@Override
	public void collect(PivotalTrackerCollector collector) {
		// TODO Auto-generated method stub
		// Collector logic
		long projectDataStart = System.currentTimeMillis();
      // pivotalclient.getProjects();
		 LOGGER.info(String.format("Inside the Pivotal Tracker Collector Task"));
        
		Set<Scope> projects =pivotalclient.getProjects() ;
		
		 projects.forEach(pivotScope -> {
	            LOGGER.info(String.format("Adding :%s-%s", pivotScope.getpId(), pivotScope.getName()));
	            pivotScope.setCollectorId(collector.getId());
	            Scope existing = projectRepository.findByCollectorIdAndPId(collector.getId(), pivotScope.getpId());
	            if (existing == null) {
	                projectRepository.save(pivotScope);
	            } else {
	                pivotScope.setId(existing.getId());
	                projectRepository.save(pivotScope);
	            }
	        });
		 log("Project Data Collected", projectDataStart, projects.size());
		 updateStoryInformation(collector);
        }
	protected void updateStoryInformation(PivotalTrackerCollector collector) {
        long storyDataStart = System.currentTimeMillis();
        AtomicLong count = new AtomicLong();

        
            List<Scope> projects = new ArrayList<>(getScopeList(collector.getId()));
            projects.forEach(project -> {
                LOGGER.info("Collecting " + count.incrementAndGet() + " of " + projects.size() + " projects.");

                long lastCollection = System.currentTimeMillis();
                FeatureEpicResult featureEpicResult = pivotalclient.getIssues(project);
                List<Feature> features = featureEpicResult.getFeatureList();
                saveFeatures(features, collector);
                //updateFeaturesWithLatestEpics(featureEpicResult.getEpicList(), collector);
                log("Story Data Collected since " + LocalDateTime.ofInstant(Instant.ofEpochMilli(project.getLastCollected()), ZoneId.systemDefault()), storyDataStart, features.size());

                project.setLastCollected(lastCollection); //set it after everything is successfully done
                projectRepository.save(project);

            });
        }

    }
	private Set<Scope> getScopeList(ObjectId collectorId) {
        Set<Scope> projects = new HashSet<>();
       
            projects = new HashSet<>(projectRepository.findByCollectorId(collectorId));
        
        return projects;
    }
	}

	

	
	
	


