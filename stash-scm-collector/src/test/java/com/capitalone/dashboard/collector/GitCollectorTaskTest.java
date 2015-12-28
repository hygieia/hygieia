package com.capitalone.dashboard.collector;

import static com.capitalone.dashboard.collector.WithinRangeMatcher.withinRange;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.scheduling.TaskScheduler;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.GitRepo;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.GitRepoRepository;

@RunWith(MockitoJUnitRunner.class)
public class GitCollectorTaskTest {

    private GitCollectorTask task;

    @Mock
    private TaskScheduler taskSchedulerMock;
    @Mock
    private BaseCollectorRepository<Collector> baseCollectorRepositoryMock;
    @Mock
    private GitRepoRepository gitRepoRepositoryMock;
    @Mock
    private CommitRepository commitRepositoryMock;
    @Mock
    private GitClient gitClientMock;
    @Mock
    private ComponentRepository componentRepositoryMock;

    @Captor
    private ArgumentCaptor<Collector> collectorArgumentCaptor;
    @Captor
    private ArgumentCaptor<List<GitRepo>> gitReposArgumentCaptor;

    @Before
    public void setUp() {
        final GitSettings gitSettings = new GitSettings();
        task =
                new GitCollectorTask(taskSchedulerMock, baseCollectorRepositoryMock, gitRepoRepositoryMock,
                    commitRepositoryMock, gitClientMock, gitSettings, componentRepositoryMock);
    }

    @Test
    public void firstRun() {
        final ObjectId id = new ObjectId();

        final Collector collector = createCollector(id);
        final List<Component> components = Collections.emptyList();
        final List<GitRepo> gitRepos = Collections.emptyList();

        when(baseCollectorRepositoryMock.save(collectorArgumentCaptor.capture())).thenReturn(collector);
        when(componentRepositoryMock.findAll()).thenReturn(components);
        when(gitRepoRepositoryMock.findByCollectorIdIn(Collections.singleton(id))).thenReturn(gitRepos);
        when(gitRepoRepositoryMock.findEnabledGitRepos(id)).thenReturn(gitRepos);

        final long startTime = System.currentTimeMillis();
        task.run();
        final long endTime = System.currentTimeMillis();

        final Collector collectorBeforeSaving = collectorArgumentCaptor.getAllValues().get(0);
        final Collector collectorAfterSaving = collectorArgumentCaptor.getAllValues().get(1);

        assertCollector(collectorBeforeSaving, null, true, 0L, 0L);
        assertCollector(collectorAfterSaving, id, false, startTime, endTime);

        verify(gitRepoRepositoryMock, times(1)).save(gitReposArgumentCaptor.capture());
        final List<GitRepo> gitRepoList = gitReposArgumentCaptor.getValue();
        assertThat(gitRepoList, Matchers.<GitRepo> empty());

        verify(baseCollectorRepositoryMock, times(1)).save(collector);
    }

    @Test
    public void afterFirstRun() {
        final ObjectId id = new ObjectId();

        final Collector collector = createCollector(id);
        final List<Component> components = createComponents(id, collector);

        when(baseCollectorRepositoryMock.findByName("Stash")).thenReturn(collector);
        when(baseCollectorRepositoryMock.save(collectorArgumentCaptor.capture())).thenReturn(collector);

        when(componentRepositoryMock.findAll()).thenReturn(components);
        when(gitRepoRepositoryMock.findByCollectorIdIn(Collections.singleton(id)))
            .thenReturn(createGitRepos(id, false));
        when(gitRepoRepositoryMock.findEnabledGitRepos(id)).thenReturn(createGitRepos(id, true));
        final Commit commit = new Commit();
        commit.setScmRevisionNumber("revision-number");

        final List<Commit> firstCall = Collections.emptyList();
        final List<Commit> secondCall = Collections.singletonList(commit);
        // noinspection unchecked
        when(gitClientMock.getCommits(isA(GitRepo.class), eq(false))).thenReturn(firstCall, secondCall);

        final long startTime = System.currentTimeMillis();
        task.run();
        final long endTime = System.currentTimeMillis();

        final List<Collector> collectors = collectorArgumentCaptor.getAllValues();

        assertThat(collectors, hasSize(2));

        final Collector collectorFromRepository = collectors.get(0);
        final Collector collectorAfterUpdate = collectors.get(1);

        assertCollector(collectorFromRepository, id, true, 0L, 0L);
        assertCollector(collectorAfterUpdate, id, false, startTime, endTime);

        verify(gitRepoRepositoryMock, times(1)).save(gitReposArgumentCaptor.capture());
        final List<GitRepo> gitRepoList = gitReposArgumentCaptor.getValue();
        assertThat(gitRepoList, hasSize(2));
        assertThat(gitRepoList.get(0).isEnabled(), is(true));
        assertThat(gitRepoList.get(1).isEnabled(), is(false));

        verify(commitRepositoryMock, times(1)).save(commit);
        verify(baseCollectorRepositoryMock, times(1)).save(collector);
    }

    private Collector createCollector(final ObjectId id) {
        final Collector collector = new Collector("Stash", CollectorType.SCM);
        collector.setEnabled(true);
        collector.setId(id);
        return collector;
    }

    private List<Component> createComponents(final ObjectId id, final Collector collector) {
        final Component component = new Component("stash");
        final CollectorItem collectorItem = new CollectorItem();
        collectorItem.setId(id);
        collectorItem.setCollectorId(id);
        collectorItem.setCollector(collector);
        component.addCollectorItem(CollectorType.SCM, collectorItem);
        return Collections.singletonList(component);
    }

    private List<GitRepo> createGitRepos(final ObjectId id, final boolean enabled) {
        final GitRepo gitRepo1 = new GitRepo();
        gitRepo1.setId(id);
        gitRepo1.setEnabled(enabled);
        gitRepo1.setLastUpdateTime(new Date());
        final GitRepo gitRepo2 = new GitRepo();
        gitRepo2.setId(new ObjectId());
        gitRepo2.setEnabled(enabled);
        gitRepo2.setLastUpdateTime(new Date());
        return Arrays.asList(gitRepo1, gitRepo2);
    }

    private void assertCollector(final Collector collector, final ObjectId id, final boolean online,
                                 final long startTime, final long endTime) {
        assertThat(collector.getId(), is(id));
        assertThat(collector.getName(), is("Stash"));
        assertThat(collector.getCollectorType(), is(CollectorType.SCM));
        assertThat(collector.isEnabled(), is(true));
        assertThat(collector.isOnline(), is(online));
        assertThat(collector.getLastExecuted(), withinRange(startTime, endTime));
    }
}
