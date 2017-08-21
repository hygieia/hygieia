package com.capitalone.dashboard.data;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.gitlab.model.GitlabIssue;
import com.capitalone.dashboard.gitlab.model.GitlabLabel;
import com.capitalone.dashboard.model.BaseModel;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.Project;
import com.capitalone.dashboard.model.UpdateResult;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.IssueItemRepository;
import com.capitalone.dashboard.repository.WidgetRepository;

@Component
public class DefaultFeatureDataClient implements FeatureDataClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFeatureDataClient.class);
	
	private final IssueItemRepository issueItemRepo;
	private final FeatureRepository featureRepo;
	private final FeatureDataMapper featureDataMapper;
	private final WidgetRepository widgetRepo;
	
	@Autowired
	public DefaultFeatureDataClient(IssueItemRepository issueRepo, FeatureDataMapper featureDataMapper, FeatureRepository featureRepo, WidgetRepository widgetRepo) {
		this.issueItemRepo = issueRepo;
		this.featureDataMapper = featureDataMapper;
		this.featureRepo = featureRepo;
		this.widgetRepo = widgetRepo;
	}

	@Override
	public UpdateResult updateIssues(Collector collector, Project project, List<GitlabIssue> issues, List<GitlabLabel> inProgressLabelsForProject) {
		List<String> inProgressLabels = new ArrayList<>();
		for(GitlabLabel label : inProgressLabelsForProject) {
			inProgressLabels.add(label.getName());
		}
		
		List<Feature> savedFeatures = issueItemRepo.getFeaturesByCollectorAndTeamNameAndProjectName(collector.getId(), project.getTeamId(), project.getProjectId());
		
		return updateAll(project, issues, collector, inProgressLabels, savedFeatures);
	}

	@SuppressWarnings("unchecked")
	private UpdateResult updateAll(Project project, List<GitlabIssue> gitlabIssues, Collector collector, List<String> inProgressLabels, List<Feature> savedFeatures) {
		
		List<Feature> updatedFeatures = new ArrayList<>();
		List<Feature> existingFeatures = new ArrayList<>();
		for(GitlabIssue issue : gitlabIssues) {
			String issueId = String.valueOf(issue.getId());
			ObjectId existingId = getExistingId(featureRepo.getFeatureIdById(issueId));
			Feature feature = featureDataMapper.mapToFeatureItem(project, issue, inProgressLabels, existingId, collector.getId());
			existingFeatures.add(feature);
    		if(updatedSinceLastRunOrFirstRun(collector.getLastExecuted(), issue, true)) {
    			updatedFeatures.add(feature);
		    }
		}
		
		Collection<Feature> deletedFeatures = CollectionUtils.subtract(savedFeatures, existingFeatures);
		
		issueItemRepo.save(updatedFeatures);
		issueItemRepo.delete(deletedFeatures);
		UpdateResult updateResult = new UpdateResult(updatedFeatures.size(), deletedFeatures.size());
				
		return updateResult;
	}
	
    private boolean updatedSinceLastRunOrFirstRun(long lastExecuted, GitlabIssue issue, boolean firstRun) {
        boolean needsUpdate = false;
        OffsetDateTime lastExecutedDate = OffsetDateTime.ofInstant(new Date(lastExecuted).toInstant(), ZoneId.systemDefault());
        // Adding 10 minutes to account for issues that could potentially be created after the issues have been collected, but before the collector finishes running.
        OffsetDateTime issueLastUpdatedDate = OffsetDateTime.parse(issue.getUpdatedAt(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).plusMinutes(10);
        if(issue.getMilestone() != null) {
            OffsetDateTime milestoneLastUpdatedDate = OffsetDateTime.parse(issue.getMilestone().getUpdatedAt(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).plusMinutes(10);
            needsUpdate = milestoneLastUpdatedDate.isAfter(lastExecutedDate);
        }
        
        return issueLastUpdatedDate.isAfter(lastExecutedDate) || needsUpdate || firstRun;
    }

    @Override
    public List<CollectorItem> getEnabledWidgets(ObjectId collectorId) {
        return widgetRepo.findByCollectorIdAndEnabled(collectorId, true);
    }
	
	private ObjectId getExistingId(List<? extends BaseModel> list) {
		if(list.size() > 1) {
			LOGGER.warn("More than one collector item found for the given Id");
		}
		
		if(!list.isEmpty()) {
			return list.get(0).getId();
		}
		
		return null;
	}

}
