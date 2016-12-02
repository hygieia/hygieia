package com.capitalone.dashboard.client.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.capitalone.dashboard.client.JiraClient;
import com.capitalone.dashboard.client.project.ProjectDataClientImpl;
import com.capitalone.dashboard.model.FeatureCollector;
import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.ScopeRepository;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import com.capitalone.dashboard.util.FeatureSettings;

@RunWith(MockitoJUnitRunner.class)
public class ProjectDataClientImplTest {
	private final ObjectId JIRA_COLLECTORID = new ObjectId("ABCDEF0123456789ABCDEF01");

	@Mock FeatureSettings featureSettings;
	@Mock ScopeRepository projectRepo;
	@Mock FeatureCollectorRepository featureCollectorRepository;
	@Mock JiraClient jiraClient;

	ProjectDataClientImpl projectDataClient;

	@Before
	public final void init() {
		projectDataClient = new ProjectDataClientImpl(featureSettings, projectRepo, featureCollectorRepository, jiraClient);

		FeatureCollector jira = new FeatureCollector();
		jira.setId(JIRA_COLLECTORID);

		Mockito.when(featureCollectorRepository.findByName(Mockito.eq(FeatureCollectorConstants.JIRA))).thenReturn(jira);
	}

	@Test
	public void testUpdateProjectInformation() {
		List<BasicProject> jiraResponse = Arrays.asList(
				new BasicProject(URI.create("http://my.jira.com/rest/api/2/project/100"), "key1", Long.valueOf(100L), "name1"),
				new BasicProject(URI.create("http://my.jira.com/rest/api/2/project/200"), "key1", Long.valueOf(200L), "name2"));

		Mockito.when(jiraClient.getProjects()).thenReturn(jiraResponse);
		ArgumentCaptor<Scope> captor = ArgumentCaptor.forClass(Scope.class);
		int cnt = projectDataClient.updateProjectInformation();

		Mockito.verify(projectRepo, Mockito.times(2)).save(captor.capture());

		assertEquals(2, cnt);

		Scope scope1 = captor.getAllValues().get(0);
		assertEquals(JIRA_COLLECTORID, scope1.getCollectorId());
		assertEquals("100", scope1.getpId());
		assertEquals("name1", scope1.getName());
		assertNotNull(scope1.getBeginDate());
		assertNotNull(scope1.getEndDate());
		assertNotNull(scope1.getChangeDate());
		assertEquals("Active", scope1.getAssetState());
		assertEquals("False", scope1.getIsDeleted());
		assertNotNull(scope1.getProjectPath());

		Scope scope2 = captor.getAllValues().get(1);
		assertEquals(JIRA_COLLECTORID, scope2.getCollectorId());
		assertEquals("200", scope2.getpId());
		assertEquals("name2", scope2.getName());
		assertNotNull(scope2.getBeginDate());
		assertNotNull(scope2.getEndDate());
		assertNotNull(scope2.getChangeDate());
		assertEquals("Active", scope2.getAssetState());
		assertEquals("False", scope2.getIsDeleted());
		assertNotNull(scope2.getProjectPath());
	}
}
