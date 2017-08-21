package com.capitalone.dashboard.data;

import java.util.ArrayList;
import java.util.Collection;
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
    public List<CollectorItem> getEnabledWidgets(ObjectId collectorId) {
        return widgetRepo.findByCollectorIdAndEnabled(collectorId, true);
    }

	@Override
	public UpdateResult updateIssues(Collector collector, Project project, List<GitlabIssue> issues, List<GitlabLabel> inProgressLabelsForProject) {
		List<String> inProgressLabels = new ArrayList<>();
		for(GitlabLabel label : inProgressLabelsForProject) {
			inProgressLabels.add(label.getName());
		}
		
		List<Feature> savedFeatures = issueItemRepo.getFeaturesByCollectorAndTeamNameAndProjectName(collector.getId(), project.getTeamId(), project.getProjectId());
		List<Feature> featuresFromGitlab = convertGitlabIssuesToFeatures(project, issues, collector, inProgressLabels);
		Collection<Feature> deletedFeatures = subtractFeatures(savedFeatures, featuresFromGitlab);
        
        issueItemRepo.save(featuresFromGitlab);
        issueItemRepo.delete(deletedFeatures);
        UpdateResult updateResult = new UpdateResult(featuresFromGitlab.size(), deletedFeatures.size());
                
        return updateResult;
		
	}

    private List<Feature> convertGitlabIssuesToFeatures(Project project, List<GitlabIssue> gitlabIssues, Collector collector, List<String> inProgressLabels) {
        List<Feature> features = new ArrayList<>();
		for(GitlabIssue issue : gitlabIssues) {
			String issueId = String.valueOf(issue.getId());
			ObjectId existingId = getExistingId(featureRepo.getFeatureIdById(issueId));
			Feature feature = featureDataMapper.mapToFeatureItem(project, issue, inProgressLabels, existingId, collector.getId());
			features.add(feature);
		}
        return features;
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
	
    @SuppressWarnings("unchecked")
    private Collection<Feature> subtractFeatures(List<Feature> savedFeatures, List<Feature> newFeatures) {
        return CollectionUtils.subtract(savedFeatures, newFeatures);
    }

}
