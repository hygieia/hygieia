package com.capitalone.dashboard.collector;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.exist;
import static com.github.dreamhead.moco.Moco.header;
import static com.github.dreamhead.moco.Moco.httpServer;
import static com.github.dreamhead.moco.Moco.not;
import static com.github.dreamhead.moco.Moco.pathResource;
import static com.github.dreamhead.moco.Moco.query;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.MocoRequestHit.once;
import static com.github.dreamhead.moco.MocoRequestHit.requestHit;
import static com.github.dreamhead.moco.MocoRequestHit.times;
import static com.github.dreamhead.moco.Runner.runner;
import static com.github.dreamhead.moco.handler.ResponseHandlers.responseHandler;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitRepo;
import com.capitalone.dashboard.util.Encryption;
import com.capitalone.dashboard.util.EncryptionException;
import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.RequestHit;
import com.github.dreamhead.moco.Runner;
import com.github.dreamhead.moco.resource.Resource;

public class DefaultStashClientTest {

    /**
     * This JSON response is an example based on Atlassian Stash documentation page.
     * 
     * @see <a href="https://developer.atlassian.com/stash/docs/latest/how-tos/command-line-rest.html">Atlassian Stash
     *      documentation page</a>
     */
    private static final Resource API_RESPONSE = pathResource("stash/response.json");
    private static final Resource PAGED_API_RESPONSE = pathResource("stash/response-with-pagination.json");

    private RequestHit requestHit;
    private HttpServer server;
    private Runner runner;

    private DefaultStashClient stashClient;
    private String encryptedPassword;

    @Before
    public void setup() throws EncryptionException {
        requestHit = requestHit();

        server = httpServer(requestHit);
        runner = runner(server);
        runner.start();

        final GitSettings settings = gitSettings();

        encryptedPassword = Encryption.encryptString("secret", settings.getKey());

        stashClient = new DefaultStashClient(settings, new RestOperationsSupplier());
    }

    @After
    public void tearDown() {
        runner.stop();
    }

    @Test
    public void firstRun() {
        server.response(API_RESPONSE);

        final List<Commit> commits = stashClient.getCommits(gitRepo(), true);

        requestHit.verify(by(uri("/repo/commits")), once());
        assertCommits(commits);
    }

    @Test
    public void afterFirstRun() {
        server.response(API_RESPONSE);

        final GitRepo repo = gitRepo();
        repo.setLastUpdateTime(new Date());

        final List<Commit> commits = stashClient.getCommits(repo, false);

        requestHit.verify(by(uri("/repo/commits")), once());
        assertCommits(commits);
    }

    // TODO Pagination doesn't work has documented.
    // See https://developer.atlassian.com/static/rest/stash/3.11.3/stash-rest.html#paging-params for more info.
    @Ignore("Implementation is different than documentation. Enable this test case after fixing the implementation.")
    @Test
    public void pagination() {
        server.get(not(exist(query("start")))).response(responseHandler(PAGED_API_RESPONSE)); // first request
        server.get(eq(query("start"), "2")).response(responseHandler(API_RESPONSE)); // second request

        final List<Commit> commits = stashClient.getCommits(gitRepo(), true);

        requestHit.verify(by(uri("/repo/commits")), times(2));
        assertThat(commits, hasSize(3));
    }

    @Test
    public void basicAuthorization() {
        server.response(API_RESPONSE);

        final GitRepo repo = gitRepo();
        repo.setPassword(encryptedPassword);

        final List<Commit> commits = stashClient.getCommits(repo, true);

        requestHit.verify(by(uri("/repo/commits")), once());
        requestHit.verify(eq(header("Authorization"), "Basic bnVsbDpzZWNyZXQ="), once());
        assertCommits(commits);
    }

    // TODO Throwing NPE doesn't seem right. Maybe this scenario (implementation) has to be handled properly.
    @Test(expected = NullPointerException.class)
    public void unexpectedResponse() {
        server.response("{}");

        stashClient.getCommits(gitRepo(), true);
    }

    // TODO Throwing NPE doesn't seem right. Maybe this scenario (implementation) has to be handled properly.
    @Test(expected = NullPointerException.class)
    public void invalidResponse() {
        server.response("{");

        stashClient.getCommits(gitRepo(), true);
    }

    private GitSettings gitSettings() throws EncryptionException {
        final GitSettings settings = new GitSettings();
        settings.setHost("localhost:");
        settings.setKey(Encryption.getStringKey());

        // TODO unfortunately the current implementation doesn't support host with port, so abusing the api settings for
        // now to get this work. Fix the implementation to support URL with port
        settings.setApi(":");

        return settings;
    }

    private GitRepo gitRepo() {
        final String repoUrl = repoUrl();

        final GitRepo repo = new GitRepo();
        repo.setRepoUrl(repoUrl);
        repo.getOptions().put("url", repoUrl);
        return repo;
    }

    private String repoUrl() {
        return String.format("http://localhost:%d/repo.git", server.port());
    }

    private void assertCommits(final List<Commit> commits) {
        final String repoUrl = repoUrl();

        assertThat(commits, hasSize(2));
        assertCommit(commits.get(0), repoUrl, "01f9c8680e9db9888463b61e423b7b1d18a5c2c1", 1334730200000L, "Author1",
            "Commit message 1");
        assertCommit(commits.get(1), repoUrl, "c9d6630b88143dab6a922c5cffe931dae68a612a", 1334639525000L, "Author2",
            "Commit message 2");
    }

    private void assertCommit(final Commit commit, final String scmUrl, final String revisionNumber,
                              final long timestamp, final String author, final String message) {
        assertThat(commit.getScmUrl(), is(scmUrl));
        assertThat(commit.getScmRevisionNumber(), is(revisionNumber));
        assertThat(commit.getScmCommitTimestamp(), is(timestamp));
        assertThat(commit.getScmAuthor(), is(author));
        assertThat(commit.getScmCommitLog(), is(message));

        // TODO This value is hardcoded to 1. Seems like Stash API doesn't provide this information. But to avoid
        // confusion can we hardcode this to '-1' a non valid value to distinguish it from a valid value. Also not sure
        // what it means by number of changes. Can it be number of files modified in a commit ? Anyways this should be
        // discussed.
        assertThat(commit.getNumberOfChanges(), is(1L));
    }
}
