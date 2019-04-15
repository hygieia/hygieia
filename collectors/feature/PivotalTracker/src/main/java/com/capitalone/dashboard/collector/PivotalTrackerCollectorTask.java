package com.capitalone.dashboard.collector;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.PivotalTrackerCollector;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.PivotalTrackerCollectorRepository;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
public class PivotalTrackerCollectorTask extends CollectorTask<PivotalTrackerCollector> {
	private final FeatureRepository featureRepository;
	 private final PivotalTrackerCollectorRepository pivotalTrackerCollectorRepository;
	
	 @Value("${apiToken}") // Injected from application.properties
     private String apiToken;
	
     @Value("${cron}") // Injected from application.properties
	 private String cron;
	 
	
	
     @Autowired
	 public PivotalTrackerCollectorTask(TaskScheduler taskScheduler,
                                         FeatureRepository featureRepository,
                                         PivotalTrackerCollectorRepository pivotalTrackerCollectorRepository) {
         super(taskScheduler, "Pivotal Tracker");
         this.featureRepository = featureRepository;
         this.pivotalTrackerCollectorRepository = pivotalTrackerCollectorRepository;
     }
     @Override
     public PivotalTrackerCollector getCollector() {

         PivotalTrackerCollector collector = new PivotalTrackerCollector();

         collector.setName("Pivotal Tracker"); // Must be unique to all collectors for a given Dashboard Application instance
         collector.setCollectorType(CollectorType.Feature);
         collector.setEnabled(true);
         collector.setApiToken(apiToken);

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
	public void collect(PivotalTrackerCollector arg0) {
		// TODO Auto-generated method stub
		// Collector logic
        PivotalTrackerApi api = new PivotalTrackerApi(apiToken);
        
        
        /*for (Project project : api.getProjects()) {

            PivotalTrackerCollectorItem collectorItem = getOrCreateCollectorItems(project.getProjectId());

            // Naive implementation
            deleteFeaturesFor(collectorItem);

            addFeaturesFor(collectorItem, project.getStories());
        }*/
	}

	

	private void deleteFeaturesFor(PivotalTrackerCollectorItem collectorItem) {
		// TODO Auto-generated method stub
		
	}
	
	private PivotalTrackerCollectorItem getOrCreateCollectorItem(long projectId) {
		return null;
        // ...
    }
	private void addFeaturesFor(PivotalTrackerCollectorItem collectorItem, List<Story> stories) {
        // ...
    }
	

}
