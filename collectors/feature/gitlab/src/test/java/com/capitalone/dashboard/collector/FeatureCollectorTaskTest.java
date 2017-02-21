package com.capitalone.dashboard.collector;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.concurrent.ListenableFuture;

import com.capitalone.dashboard.gitlab.model.GitlabProject;
import com.capitalone.dashboard.model.FeatureCollector;
import com.capitalone.dashboard.model.UpdateResult;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;

@RunWith(MockitoJUnitRunner.class)
public class FeatureCollectorTaskTest {

	@Mock
	private FeatureCollectorRepository featureRepo;
	
	@Mock
	private FeatureSettings settings;
	
	@Mock 
	private FeatureService featureService;
	
	@Mock
	private ListenableFuture<UpdateResult> future;
	
	@InjectMocks
	private FeatureCollectorTask featureCollectorTask;
	
	@Test
	public void shouldGetCollector() {
		FeatureCollector collector = featureCollectorTask.getCollector();
		assertNotNull(collector);
		assertTrue(FeatureCollector.class == collector.getClass());
	}
	
	@Test
	public void shouldGetCollectorRepo() {
		assertSame(featureRepo, featureCollectorTask.getCollectorRepository());
	}
	
	@Test
	public void shouldGetCron() {
		String cron = "cron";
		when(settings.getCron()).thenReturn(cron);
		assertSame(cron, featureCollectorTask.getCron());
	}
	
	@Test
	public void shouldCollect() throws InterruptedException, ExecutionException {
		ObjectId id = new ObjectId();
		long lastExecuted = 1;
		FeatureCollector collector = new FeatureCollector();
		collector.setId(id);
		collector.setLastExecuted(lastExecuted);
		List<GitlabProject> projects = new ArrayList<>();
		projects.add(new GitlabProject());
		when(featureService.getEnabledProjects(id)).thenReturn(projects);
		when(featureService.updateSelectableTeams(id)).thenReturn(future);
		when(featureService.updateProjects(id)).thenReturn(future);
		when(featureService.updateIssuesForProject(eq(id), eq(lastExecuted), isA(GitlabProject.class))).thenReturn(future);
		when(future.get()).thenReturn(new UpdateResult(1, 1));
		
		featureCollectorTask.collect(collector);
		
		verify(featureService).updateSelectableTeams(id);
		verify(featureService).updateProjects(id);
		verify(featureService).updateIssuesForProject(id, lastExecuted, projects.get(0));
	}
	
	@Test
	public void shouldLogException() throws InterruptedException, ExecutionException {
		ObjectId id = new ObjectId();
		long lastExecuted = 1;
		FeatureCollector collector = new FeatureCollector();
		collector.setId(id);
		collector.setLastExecuted(lastExecuted);
		List<GitlabProject> projects = new ArrayList<>();
		projects.add(new GitlabProject());
		when(featureService.getEnabledProjects(id)).thenReturn(projects);
		when(featureService.updateSelectableTeams(id)).thenReturn(future);
		when(featureService.updateProjects(id)).thenReturn(future);
		when(featureService.updateIssuesForProject(eq(id), eq(lastExecuted), isA(GitlabProject.class))).thenReturn(future);
		when(future.get()).thenThrow(new InterruptedException());
		
		featureCollectorTask.collect(collector);
		
		verify(featureService).updateSelectableTeams(id);
		verify(featureService).updateProjects(id);
		verify(featureService).updateIssuesForProject(id, lastExecuted, projects.get(0));
	}

}
