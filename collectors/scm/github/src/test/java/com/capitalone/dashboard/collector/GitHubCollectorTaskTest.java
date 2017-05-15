package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.CollectionError;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.GitHubRepo;
import com.capitalone.dashboard.repository.BaseCollectorItemRepository;
import com.capitalone.dashboard.repository.CommitRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.GitHubRepoRepository;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GitHubCollectorTaskTest {

    @Mock private BaseCollectorItemRepository collectors;
    @Mock private GitHubRepoRepository gitHubRepoRepository;
    @Mock private GitHubClient gitHubClient;
    @Mock private GitHubSettings gitHubSettings;
    @Mock private ComponentRepository dbComponentRepository;
    @Mock private CommitRepository commitRepository;

    @Mock private GitHubRepo repo1;
    @Mock private GitHubRepo repo2;

    @Mock private Commit commit;

    @InjectMocks private GitHubCollectorTask task;

    @Test
    public void collect_testCollect() {
        when(dbComponentRepository.findAll()).thenReturn(components());

        Set<ObjectId> gitID = new HashSet<>();
        gitID.add(new ObjectId("111ca42a258ad365fbb64ecc"));
        when(gitHubRepoRepository.findByCollectorIdIn(gitID)).thenReturn(getGitHubs());

        Collector collector = new Collector();
        collector.setEnabled(true);
        collector.setName("collector");
        collector.setId(new ObjectId("111ca42a258ad365fbb64ecc"));

        when(gitHubRepoRepository.findEnabledGitHubRepos(collector.getId())).thenReturn(getEnabledRepos());

        when(gitHubSettings.getErrorThreshold()).thenReturn(1);

        when(gitHubClient.getCommits(repo1, true)).thenReturn(getCommits());

        when(commitRepository.findByCollectorItemIdAndScmRevisionNumber(
                repo1.getId(), "1")).thenReturn(null);

        task.collect(collector);

        //verify that orphaned repo is disabled
        assertEquals("repo2.no.collectoritem", repo2.getNiceName());
        assertEquals(false, repo2.isEnabled());

        //verify that repo1 is enabled
        assertEquals("repo1-ci1", repo1.getNiceName());
        assertEquals(true, repo1.isEnabled());

        //verify that save is called once for the commit item
        Mockito.verify(commitRepository, times(1)).save(commit);
    }


    @Test
    public void collect_testCollect_with_Threshold_0() {
        when(dbComponentRepository.findAll()).thenReturn(components());

        Set<ObjectId> gitID = new HashSet<>();
        gitID.add(new ObjectId("111ca42a258ad365fbb64ecc"));
        when(gitHubRepoRepository.findByCollectorIdIn(gitID)).thenReturn(getGitHubs());

        Collector collector = new Collector();
        collector.setEnabled(true);
        collector.setName("collector");
        collector.setId(new ObjectId("111ca42a258ad365fbb64ecc"));

        when(gitHubRepoRepository.findEnabledGitHubRepos(collector.getId())).thenReturn(getEnabledRepos());

        when(gitHubSettings.getErrorThreshold()).thenReturn(0);

        when(gitHubClient.getCommits(repo1, true)).thenReturn(getCommits());

        when(commitRepository.findByCollectorItemIdAndScmRevisionNumber(
                repo1.getId(), "1")).thenReturn(null);

        task.collect(collector);

        //verify that orphaned repo is disabled
        assertEquals("repo2.no.collectoritem", repo2.getNiceName());
        assertEquals(false, repo2.isEnabled());

        //verify that repo1 is enabled
        assertEquals("repo1-ci1", repo1.getNiceName());
        assertEquals(true, repo1.isEnabled());

        //verify that save is called once for the commit item
        Mockito.verify(commitRepository, times(0)).save(commit);
    }

    @Test
    public void collect_testCollect_with_Threshold_1() {
        when(dbComponentRepository.findAll()).thenReturn(components());

        Set<ObjectId> gitID = new HashSet<>();
        gitID.add(new ObjectId("111ca42a258ad365fbb64ecc"));
        when(gitHubRepoRepository.findByCollectorIdIn(gitID)).thenReturn(getGitHubs());

        Collector collector = new Collector();
        collector.setEnabled(true);
        collector.setName("collector");
        collector.setId(new ObjectId("111ca42a258ad365fbb64ecc"));

        when(gitHubRepoRepository.findEnabledGitHubRepos(collector.getId())).thenReturn(getEnabledRepos());

        when(gitHubSettings.getErrorThreshold()).thenReturn(1);

        when(gitHubClient.getCommits(repo1, true)).thenReturn(getCommits());

        when(commitRepository.findByCollectorItemIdAndScmRevisionNumber(
                repo1.getId(), "1")).thenReturn(null);

        task.collect(collector);

        //verify that orphaned repo is disabled
        assertEquals("repo2.no.collectoritem", repo2.getNiceName());
        assertEquals(false, repo2.isEnabled());

        //verify that repo1 is enabled
        assertEquals("repo1-ci1", repo1.getNiceName());
        assertEquals(true, repo1.isEnabled());

        //verify that save is called once for the commit item
        Mockito.verify(commitRepository, times(1)).save(commit);
    }

    @Test
    public void collect_testCollect_with_Threshold_1_Error_1() {
        when(dbComponentRepository.findAll()).thenReturn(components());

        Set<ObjectId> gitID = new HashSet<>();
        gitID.add(new ObjectId("111ca42a258ad365fbb64ecc"));
        when(gitHubRepoRepository.findByCollectorIdIn(gitID)).thenReturn(getGitHubs());

        Collector collector = new Collector();
        collector.setEnabled(true);
        collector.setName("collector");
        collector.setId(new ObjectId("111ca42a258ad365fbb64ecc"));

        when(gitHubRepoRepository.findEnabledGitHubRepos(collector.getId())).thenReturn(getEnabledReposWithErrorCount1());

        when(gitHubSettings.getErrorThreshold()).thenReturn(1);

        when(gitHubClient.getCommits(repo1, true)).thenReturn(getCommits());

        when(commitRepository.findByCollectorItemIdAndScmRevisionNumber(
                repo1.getId(), "1")).thenReturn(null);

        task.collect(collector);

        //verify that orphaned repo is disabled
        assertEquals("repo2.no.collectoritem", repo2.getNiceName());
        assertEquals(false, repo2.isEnabled());

        //verify that repo1 is enabled
        assertEquals("repo1-ci1", repo1.getNiceName());
        assertEquals(true, repo1.isEnabled());

        //verify that save is called once for the commit item
        Mockito.verify(commitRepository, times(0)).save(commit);
    }

    private ArrayList<Commit> getCommits() {
        ArrayList<Commit> commits = new ArrayList<Commit>();
        commit = new Commit();
        commit.setTimestamp(System.currentTimeMillis());
        commit.setScmUrl("http://testcurrenturl");
        commit.setScmBranch("master");
        commit.setScmRevisionNumber("1");
        commit.setScmParentRevisionNumbers(Collections.singletonList("2"));
        commit.setScmAuthor("author");
        commit.setScmCommitLog("This is a test commit");
        commit.setScmCommitTimestamp(System.currentTimeMillis());
        commit.setNumberOfChanges(1);
        commit.setType(CommitType.New);
        commits.add(commit);
        return commits;
    }

    private List<GitHubRepo> getEnabledRepos() {
        List<GitHubRepo> gitHubs = new ArrayList<GitHubRepo>();
        repo1 = new GitHubRepo();
        repo1.setEnabled(true);
        repo1.setId(new ObjectId("1c1ca42a258ad365fbb64ecc"));
        repo1.setCollectorId(new ObjectId("111ca42a258ad365fbb64ecc"));
        repo1.setNiceName("repo1-ci1");
        repo1.setRepoUrl("http://current");
        gitHubs.add(repo1);
        return gitHubs;
    }

    private List<GitHubRepo> getEnabledReposWithErrorCount1() {
        List<GitHubRepo> gitHubs = new ArrayList<GitHubRepo>();
        repo1 = new GitHubRepo();
        repo1.setEnabled(true);
        repo1.setId(new ObjectId("1c1ca42a258ad365fbb64ecc"));
        repo1.setCollectorId(new ObjectId("111ca42a258ad365fbb64ecc"));
        repo1.setNiceName("repo1-ci1");
        repo1.setRepoUrl("http://current");
        CollectionError error = new CollectionError("Error","Error");
        repo1.getErrors().add(error);
        gitHubs.add(repo1);
        return gitHubs;
    }

    private ArrayList<GitHubRepo> getGitHubs() {
        ArrayList<GitHubRepo> gitHubs = new ArrayList<GitHubRepo>();

        repo1 = new GitHubRepo();
        repo1.setEnabled(true);
        repo1.setId(new ObjectId("1c1ca42a258ad365fbb64ecc"));
        repo1.setCollectorId(new ObjectId("111ca42a258ad365fbb64ecc"));
        repo1.setNiceName("repo1-ci1");
        repo1.setRepoUrl("http://current");

        repo2 = new GitHubRepo();
        repo2.setEnabled(true);
        repo2.setId(new ObjectId("1c4ca42a258ad365fbb64ecc"));
        repo2.setCollectorId(new ObjectId("111ca42a258ad365fbb64ecc"));
        repo2.setNiceName("repo2.no.collectoritem");
        repo2.setRepoUrl("http://obsolete");

        gitHubs.add(repo1);
        gitHubs.add(repo2);

        return gitHubs;
    }

    private ArrayList<com.capitalone.dashboard.model.Component> components() {
        ArrayList<com.capitalone.dashboard.model.Component> cArray = new ArrayList<com.capitalone.dashboard.model.Component>();
        com.capitalone.dashboard.model.Component c = new Component();
        c.setId(new ObjectId());
        c.setName("COMPONENT1");
        c.setOwner("JOHN");

        CollectorType scmType = CollectorType.SCM;
        CollectorItem ci1 = new CollectorItem();
        ci1.setId(new ObjectId("1c1ca42a258ad365fbb64ecc"));
        ci1.setNiceName("ci1");
        ci1.setEnabled(true);
        ci1.setPushed(false);
        ci1.setCollectorId(new ObjectId("111ca42a258ad365fbb64ecc"));
        c.addCollectorItem(scmType, ci1);

        CollectorItem ci2 = new CollectorItem();
        ci2.setId(new ObjectId("1c2ca42a258ad365fbb64ecc"));
        ci2.setNiceName("ci2");
        ci2.setEnabled(true);
        ci2.setPushed(false);
        ci2.setCollectorId(new ObjectId("111ca42a258ad365fbb64ecc"));
        c.addCollectorItem(scmType, ci2);

        CollectorItem ci3 = new CollectorItem();
        ci3.setId(new ObjectId("1c3ca42a258ad365fbb64ecc"));
        ci3.setNiceName("ci3");
        ci3.setEnabled(true);
        ci3.setPushed(false);
        ci3.setCollectorId(new ObjectId("222ca42a258ad365fbb64ecc"));
        c.addCollectorItem(scmType, ci3);

        cArray.add(c);

        return cArray;
    }
}