package com.capitalone.dashboard.client.team;

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
import com.capitalone.dashboard.model.FeatureCollector;
import com.capitalone.dashboard.model.ScopeOwnerCollectorItem;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.ScopeOwnerRepository;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import com.capitalone.dashboard.util.FeatureSettings;

@RunWith(MockitoJUnitRunner.class)
public class TeamDataClientImplTest {
	private final ObjectId JIRA_COLLECTORID = new ObjectId("ABCDEF0123456789ABCDEF01");
	
	@Mock FeatureSettings featureSettings;
	@Mock ScopeOwnerRepository teamRepo;
	@Mock FeatureCollectorRepository featureCollectorRepository;
	@Mock JiraClient jiraClient;
	
	TeamDataClientImpl teamDataClient;
	
	@Before
	public final void init() {
		teamDataClient = new TeamDataClientImpl(featureCollectorRepository, featureSettings, teamRepo, jiraClient);
		
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
		ArgumentCaptor<ScopeOwnerCollectorItem> captor = ArgumentCaptor.forClass(ScopeOwnerCollectorItem.class);
		int cnt = teamDataClient.updateTeamInformation();

		Mockito.verify(teamRepo, Mockito.times(2)).save(captor.capture());
		
		assertEquals(2, cnt);

		ScopeOwnerCollectorItem soci1 = captor.getAllValues().get(0);
		assertEquals(JIRA_COLLECTORID, soci1.getCollectorId());
		assertEquals("100", soci1.getTeamId());
		assertEquals("name1", soci1.getName());
		assertNotNull(soci1.getChangeDate());
		assertEquals("Active", soci1.getAssetState());
		assertEquals("False", soci1.getIsDeleted());
		
		ScopeOwnerCollectorItem soci2 = captor.getAllValues().get(1);
		assertEquals(JIRA_COLLECTORID, soci2.getCollectorId());
		assertEquals("200", soci2.getTeamId());
		assertEquals("name2", soci2.getName());
		assertNotNull(soci2.getChangeDate());
		assertEquals("Active", soci2.getAssetState());
		assertEquals("False", soci2.getIsDeleted());
	}
}
