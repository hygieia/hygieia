package com.capitalone.dashboard.data;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.capitalone.dashboard.gitlab.model.GitlabIssue;
import com.capitalone.dashboard.gitlab.model.GitlabLabel;
import com.capitalone.dashboard.gitlab.model.GitlabMilestone;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.FeatureCollector;
import com.capitalone.dashboard.model.Project;
import com.capitalone.dashboard.model.UpdateResult;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.IssueItemRepository;
import com.capitalone.dashboard.repository.WidgetRepository;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class DefaultFeatureDataClientTest {
	
	@Mock
	private FeatureCollectorRepository featureCollectorRepo;
	
	@Mock
	private IssueItemRepository issueItemRepo;
	
	@Mock
	private FeatureRepository featureRepo;
	
	@Mock
	private FeatureDataMapper featureDataMapper;
	
	@Mock
	private FeatureCollector collector;
	
	@Mock
	private WidgetRepository widgetRepo;
	
	@InjectMocks
	private DefaultFeatureDataClient featureDataClient;
	
	private ObjectId collectorId;
	
	@Before
	public void setup() {
		collectorId = new ObjectId();
		when(featureCollectorRepo.findByName(FeatureCollectorConstants.GITLAB)).thenReturn(collector);
		when(collector.getId()).thenReturn(collectorId);
	}
	
	@Test
	public void shouldUpdateIssues() {
	    String teamId = "capitalone";
        String projectId = "hygieia";
        Project project = new Project(teamId, projectId);
        Collector collector = new Collector();
        collector.setId(collectorId);
        collector.setLastExecuted(1487688565442L);
		
		GitlabIssue gitlabIssue = new GitlabIssue();
		gitlabIssue.setId(1L);
		gitlabIssue.setUpdatedAt("2018-02-20T12:00:00+01:00");
		List<String> labels = Lists.newArrayList("To Do");
		gitlabIssue.setLabels(labels);
		
		GitlabIssue gitlabIssue2 = new GitlabIssue();
        gitlabIssue2.setId(1L);
        gitlabIssue2.setUpdatedAt("2016-02-20T12:00:00+01:00");
        gitlabIssue2.setLabels(labels);
        GitlabMilestone milestone = new GitlabMilestone();
        milestone.setUpdatedAt("2018-02-20T12:00:00+01:00");
        gitlabIssue2.setMilestone(milestone);
		
		List<GitlabIssue> issues = Lists.newArrayList(gitlabIssue, gitlabIssue2);
		GitlabLabel gitlabLabel = new GitlabLabel();
		gitlabLabel.setName("To Do");
		List<GitlabLabel> inProgressLabelsForProject = Lists.newArrayList(gitlabLabel);
		Feature savedFeature = new Feature();
		savedFeature.setsId("6");
		savedFeature.setCollectorId(collectorId);
		List<Feature> savedFeatures = Lists.newArrayList(savedFeature );
		when(issueItemRepo.getFeaturesByCollectorAndTeamNameAndProjectName(collectorId, project.getTeamId(), project.getProjectId())).thenReturn(savedFeatures);
	        
	    UpdateResult result = featureDataClient.updateIssues(collector, project, issues , inProgressLabelsForProject );
	        
		assertEquals(1, result.getItemsDeleted());
		assertEquals(2, result.getItemsAdded());
	}
	

	@Test
	public void shouldFindEnabledWidgets() {
		featureDataClient.getEnabledWidgets(collectorId);
		
		verify(widgetRepo, times(1)).findByCollectorIdAndEnabled(collectorId, true);
	}

}
