package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.*;
import com.google.common.collect.Sets;
import com.sun.org.apache.xpath.internal.operations.Gt;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.scheduling.TaskScheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
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

    @InjectMocks private GitHubCollectorTask task;

    @Test
    public void collect_testCollect() {
        when(dbComponentRepository.findAll()).thenReturn(components());

        Set<ObjectId> gitID = new HashSet<>();
        gitID.add(new ObjectId("111ca42a258ad365fbb64ecc"));
        when(gitHubRepoRepository.findByCollectorIdIn(gitID)).thenReturn(getGitHubs());

        Collector collector = new Collector();
        collector.setEnabled(true);
        collector.setId(new ObjectId("111ca42a258ad365fbb64ecc"));

        when(gitHubRepoRepository.findEnabledGitHubRepos(collector.getId())).thenReturn(getEnabledRepos());

        when(gitHubClient.getCommits(repo1, true)).thenReturn(getCommits());

        when(commitRepository.findByCollectorItemIdAndScmRevisionNumber(
                repo1.getId(), "1")).thenReturn(null);

        task.collect(collector);

        boolean expected = false;
        assertEquals(expected, repo2.isEnabled());
    }

    private ArrayList<Commit> getCommits() {
        ArrayList<Commit> commits = new ArrayList<Commit>();
        Commit commit = new Commit();
        commit.setTimestamp(System.currentTimeMillis());
        commit.setScmUrl("http://testcurrenturl");
        commit.setScmBranch("master");
        commit.setScmRevisionNumber("1");
        commit.setScmAuthor("satish");
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
        repo1.setRepoUrl("http://current");
        gitHubs.add(repo1);
        return gitHubs;
    }

    private ArrayList<GitHubRepo> getGitHubs() {
        ArrayList<GitHubRepo> gitHubs = new ArrayList<GitHubRepo>();

        repo1 = new GitHubRepo();
        repo1.setEnabled(true);
        repo1.setId(new ObjectId("1c1ca42a258ad365fbb64ecc"));
        repo1.setCollectorId(new ObjectId("111ca42a258ad365fbb64ecc"));
        repo1.setRepoUrl("http://current");

        repo2 = new GitHubRepo();
        repo2.setEnabled(true);
        repo2.setId(new ObjectId("1c4ca42a258ad365fbb64ecc"));
        repo2.setCollectorId(new ObjectId("111ca42a258ad365fbb64ecc"));
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
        ci1.setEnabled(true);
        ci1.setPushed(false);
        ci1.setCollectorId(new ObjectId("111ca42a258ad365fbb64ecc"));
        c.addCollectorItem(scmType, ci1);

        CollectorItem ci2 = new CollectorItem();
        ci2.setId(new ObjectId("1c2ca42a258ad365fbb64ecc"));
        ci2.setEnabled(true);
        ci2.setPushed(false);
        ci2.setCollectorId(new ObjectId("111ca42a258ad365fbb64ecc"));
        c.addCollectorItem(scmType, ci2);

        CollectorItem ci3 = new CollectorItem();
        ci3.setId(new ObjectId("1c3ca42a258ad365fbb64ecc"));
        ci3.setEnabled(true);
        ci3.setPushed(false);
        ci3.setCollectorId(new ObjectId("222ca42a258ad365fbb64ecc"));
        c.addCollectorItem(scmType, ci3);

        cArray.add(c);

        return cArray;
    }
}