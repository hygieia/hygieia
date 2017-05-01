package com.capitalone.dashboard.collector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.capitalone.dashboard.collector.GitlabGitCollectorTask;
import com.capitalone.dashboard.collector.GitlabSettings;
import com.capitalone.dashboard.gitlab.DefaultGitlabGitClient;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
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
	
	@Mock
	private GitlabGitRepo gitlabGitRepo;
	
	@Mock
	private Commit commit;
	
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
	
	@Test
	public void shouldFindNoCommits() {
		when(componentRepository.findAll()).thenReturn(new ArrayList<>());
		when(gitlabGitCollectorRepository.findByCollectorIdIn(anyCollection())).thenReturn(new ArrayList<>());
		when(collector.getId()).thenReturn(new ObjectId());
		List<GitlabGitRepo> enabledRepos = new ArrayList<>();
		enabledRepos.add(gitlabGitRepo);
		when(gitlabGitCollectorRepository.findEnabledGitlabRepos(isA(ObjectId.class))).thenReturn(enabledRepos);
		when(gitlabGitRepo.getLastUpdated()).thenReturn(0L);
		
		gitlabGitCollectorTask.collect(collector);
		
		verify(gitlabGitCollectorRepository, times(1)).save(gitlabGitRepo);
		verify(commitRepository, never()).save(isA(Commit.class));
	}
	
	@Test
	public void shouldFindOneExistingCommit() {
		when(componentRepository.findAll()).thenReturn(new ArrayList<>());
		when(gitlabGitCollectorRepository.findByCollectorIdIn(anyCollection())).thenReturn(new ArrayList<>());
		when(collector.getId()).thenReturn(new ObjectId());
		List<GitlabGitRepo> enabledRepos = new ArrayList<>();
		enabledRepos.add(gitlabGitRepo);
		when(gitlabGitCollectorRepository.findEnabledGitlabRepos(isA(ObjectId.class))).thenReturn(enabledRepos);
		when(gitlabGitRepo.getLastUpdated()).thenReturn(1477513100920L);
		ArrayList<Commit> commits = new ArrayList<>();
		commits.add(commit);
		when(defaultGitlabGitClient.getCommits(isA(GitlabGitRepo.class), anyBoolean())).thenReturn(commits);
		when(gitlabGitRepo.getId()).thenReturn(new ObjectId());
		when(commit.getScmRevisionNumber()).thenReturn("12");
		when(commitRepository.findByCollectorItemIdAndScmRevisionNumber(isA(ObjectId.class), anyString())).thenReturn(new Commit());
		
		gitlabGitCollectorTask.collect(collector);
		
		verify(gitlabGitCollectorRepository, times(1)).save(gitlabGitRepo);
		verify(commitRepository, never()).save(isA(Commit.class));
	}
	
	@Test
	public void shouldFindOneNewCommit() {
		when(componentRepository.findAll()).thenReturn(new ArrayList<>());
		when(gitlabGitCollectorRepository.findByCollectorIdIn(anyCollection())).thenReturn(new ArrayList<>());
		when(collector.getId()).thenReturn(new ObjectId());
		List<GitlabGitRepo> enabledRepos = new ArrayList<>();
		enabledRepos.add(gitlabGitRepo);
		when(gitlabGitCollectorRepository.findEnabledGitlabRepos(isA(ObjectId.class))).thenReturn(enabledRepos);
		when(gitlabGitRepo.getLastUpdated()).thenReturn(1477513100920L);
		ArrayList<Commit> commits = new ArrayList<>();
		commits.add(commit);
		when(defaultGitlabGitClient.getCommits(isA(GitlabGitRepo.class), anyBoolean())).thenReturn(commits);
		when(gitlabGitRepo.getId()).thenReturn(new ObjectId());
		when(commit.getScmRevisionNumber()).thenReturn("12");
		when(commitRepository.findByCollectorItemIdAndScmRevisionNumber(isA(ObjectId.class), anyString())).thenReturn(null);
		
		gitlabGitCollectorTask.collect(collector);
		
		verify(gitlabGitCollectorRepository, times(1)).save(gitlabGitRepo);
		verify(commitRepository, times(1)).save(commit);
	}
	
	@Test
	public void shouldNotSaveRepoWhenClientError() {
		when(componentRepository.findAll()).thenReturn(new ArrayList<>());
		when(gitlabGitCollectorRepository.findByCollectorIdIn(anyCollection())).thenReturn(new ArrayList<>());
		when(collector.getId()).thenReturn(new ObjectId());
		List<GitlabGitRepo> enabledRepos = new ArrayList<>();
		enabledRepos.add(gitlabGitRepo);
		when(gitlabGitCollectorRepository.findEnabledGitlabRepos(isA(ObjectId.class))).thenReturn(enabledRepos);
		when(gitlabGitRepo.getLastUpdated()).thenReturn(1477513100920L);
		when(defaultGitlabGitClient.getCommits(isA(GitlabGitRepo.class), anyBoolean())).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
		when(gitlabGitRepo.getId()).thenReturn(new ObjectId());
		when(commit.getScmRevisionNumber()).thenReturn("12");
		when(commitRepository.findByCollectorItemIdAndScmRevisionNumber(isA(ObjectId.class), anyString())).thenReturn(null);
		
		gitlabGitCollectorTask.collect(collector);
		
		verify(gitlabGitCollectorRepository, never()).save(gitlabGitRepo);
		verify(commitRepository, never()).save(commit);
	}
	
	@Test
	public void shouldNotSaveRepoWhenResourceAccessExceptin() {
		when(componentRepository.findAll()).thenReturn(new ArrayList<>());
		when(gitlabGitCollectorRepository.findByCollectorIdIn(anyCollection())).thenReturn(new ArrayList<>());
		when(collector.getId()).thenReturn(new ObjectId());
		List<GitlabGitRepo> enabledRepos = new ArrayList<>();
		enabledRepos.add(gitlabGitRepo);
		when(gitlabGitCollectorRepository.findEnabledGitlabRepos(isA(ObjectId.class))).thenReturn(enabledRepos);
		when(gitlabGitRepo.getLastUpdated()).thenReturn(1477513100920L);
		when(defaultGitlabGitClient.getCommits(isA(GitlabGitRepo.class), anyBoolean())).thenThrow(new ResourceAccessException("Bad"));
		when(gitlabGitRepo.getId()).thenReturn(new ObjectId());
		when(commit.getScmRevisionNumber()).thenReturn("12");
		when(commitRepository.findByCollectorItemIdAndScmRevisionNumber(isA(ObjectId.class), anyString())).thenReturn(null);
		
		gitlabGitCollectorTask.collect(collector);
		
		verify(gitlabGitCollectorRepository, never()).save(gitlabGitRepo);
		verify(commitRepository, never()).save(commit);
	}

}
