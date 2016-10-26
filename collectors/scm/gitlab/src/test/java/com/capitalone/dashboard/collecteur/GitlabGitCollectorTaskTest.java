package com.capitalone.dashboard.collecteur;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.GitlabGitRepo;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.GitlabGitCollectorRepository;

@RunWith(MockitoJUnitRunner.class)
public class GitlabGitCollectorTaskTest {
	
	@Mock
	private BaseCollectorRepository<Collector> collectorRepository;
	
	@Mock
	private GitlabGitCollectorRepository gitlabGitCollectorRepository;
	
	@Mock
	private GitlabSettings gitlabSettings;
	
	@Mock
	private DefaultGitlabGitClient defaultGitlabGitClient;
	
	@Mock
	private ComponentRepository componentRepository;
	
	@Mock
	private CommitRepository commitRepository;
	
	@Mock
	private Collector collector;
	
	@InjectMocks
	private GitlabGitCollectorTask gitlabGitCollectorTask;

	@Test
	public void shouldGetCollector() {
		Collector collector = gitlabGitCollectorTask.getCollector();
		
		assertEquals("Gitlab", collector.getName());
		assertEquals(CollectorType.SCM, collector.getCollectorType());
		assertTrue(collector.isOnline());
		assertTrue(collector.isEnabled());
	}
	
	@Test
	public void shouldGetCollectorRepository() {
		assertSame(collectorRepository, gitlabGitCollectorTask.getCollectorRepository());
	}
	
	@Test
	public void shouldGetChron() {
		when(gitlabSettings.getCron()).thenReturn("cron");
		
		assertEquals("cron", gitlabGitCollectorTask.getCron());
	}
	
	@Test
	public void shouldNotFindAnyEnabledRepos() {
		when(componentRepository.findAll()).thenReturn(new ArrayList<>());
		when(gitlabGitCollectorRepository.findByCollectorIdIn(anyCollection())).thenReturn(new ArrayList<>());
		when(gitlabGitCollectorRepository.findEnabledGitlabRepos(isA(ObjectId.class))).thenReturn(new ArrayList<>());
		
		gitlabGitCollectorTask.collect(collector);
		
		verify(gitlabGitCollectorRepository, never()).save(isA(GitlabGitRepo.class));
		
	}

}
