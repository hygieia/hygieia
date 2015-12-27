package com.capitalone.dashboard.collector;

import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.header;
import static com.github.dreamhead.moco.Moco.httpServer;
import static com.github.dreamhead.moco.Moco.pathResource;
import static com.github.dreamhead.moco.MocoRequestHit.requestHit;
import static com.github.dreamhead.moco.MocoRequestHit.times;
import static com.github.dreamhead.moco.Runner.runner;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
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
     * This JSON response is an example response based on Atlassian documentation page.
     * 
     * @see <a href="https://developer.atlassian.com/stash/docs/latest/how-tos/command-line-rest.html">Atlassian
     *      documentation page</a>
     */
    private static final Resource COMMITS_API_RESPONSE = pathResource("stash-api-commits-response.json");

    private int port;
    private RequestHit requestHit;
    private HttpServer server;
    private Runner runner;

    private DefaultStashClient client;
    private String encryptedPassword;

    @Before
    public void setup() throws EncryptionException {
        port = port();
        requestHit = requestHit();

        server = httpServer(port, requestHit);
        runner = runner(server);
        runner.start();

        final GitSettings settings = new GitSettings();
        settings.setHost("localhost:12306/");
        settings.setKey(Encryption.getStringKey());
        settings.setApi(":"); // TODO unfortunately the current implementation doesn't support host with port, so
                              // abusing the api settings for now to get this work. Fix the implementation to support
                              // URL with port

        encryptedPassword = Encryption.encryptString("secret", settings.getKey());

        client = new DefaultStashClient(settings, new RestOperationsSupplier());
    }

    @After
    public void tearDown() {
        runner.stop();
    }

    @Test
    public void firstRun() {
        server.response(COMMITS_API_RESPONSE);

        final List<Commit> commits = client.getCommits(gitRepo(), true);

        assertCommits(commits);
    }

    @Test
    public void notFirstRun() {
        server.response(COMMITS_API_RESPONSE);

        final GitRepo repo = gitRepo();
        repo.setLastUpdateTime(new Date());

        final List<Commit> commits = client.getCommits(repo, false);

        assertCommits(commits);
    }

    @Test
    public void basicAuthorization() {
        server.response(COMMITS_API_RESPONSE);

        final GitRepo repo = gitRepo();
        repo.setPassword(encryptedPassword);

        final List<Commit> commits = client.getCommits(repo, true);

        requestHit.verify(eq(header("Authorization"), "Basic bnVsbDpzZWNyZXQ="), times(1));
        assertCommits(commits);
    }

    // TODO NPE doesn't sound good. Maybe this scenario (implementation) has to be handled properly.
    @Test(expected = NullPointerException.class)
    public void invalidResponse() {
        server.response("{}");

        client.getCommits(gitRepo(), true);
    }

    private int port() {
        int port = 12306;
        for (int i = 0; i < 100; i++) {
            if (portAvailable(port)) {
                return port;
            }

            port++;
        }

        throw new IllegalStateException("Unable to find a free port");
    }

    private boolean portAvailable(int port) {
        try (final Socket ignored = new Socket("localhost", port)) {
            return false;
        } catch (final IOException ignored) {
            return true;
        }
    }

    private GitRepo gitRepo() {
        final String repoUrl = repoUrl();

        final GitRepo repo = new GitRepo();
        repo.setRepoUrl(repoUrl);
        repo.getOptions().put("url", repoUrl);
        return repo;
    }

    private String repoUrl() {
        return String.format("http://localhost:%d/repo.git", port);
    }

    private void assertCommits(final List<Commit> commits) {
        final String repoUrl = repoUrl();

        assertThat(commits, hasSize(2));
        assertCommit(commits.get(0), repoUrl, "01f9c8680e9db9888463b61e423b7b1d18a5c2c1", 1334730200000L, "Seb Ruiz",
            "NONE: Add groovy as java synhi\n+review @aahmed");
        assertCommit(commits.get(1), repoUrl, "c9d6630b88143dab6a922c5cffe931dae68a612a", 1334639525000L,
            "Pierre-Etienne Poirot", "STASH-2121: fix typo (CR-STASH-1199)");
    }

    private void assertCommit(final Commit commit, final String scmUrl, final String revisionNumber,
                              final long timestamp, final String author, final String message) {
        assertThat(commit.getScmUrl(), is(scmUrl));
        assertThat(commit.getScmRevisionNumber(), is(revisionNumber));
        assertThat(commit.getScmCommitTimestamp(), is(timestamp));
        assertThat(commit.getScmAuthor(), is(author));
        assertThat(commit.getScmCommitLog(), is(message));
        assertThat(commit.getNumberOfChanges(), is(1L)); // TODO This value is hardcoded to 1. Seems like Stash API
                                                         // doesn't provide this information. But to avoid confusion can
                                                         // we hardcode this to '-1' a non valid value to distinguish
                                                         // it from a valid value. Also not sure what it means by number
                                                         // of changes. Can it be number of files modified in a commit ?
                                                         // Anyways this should be discussed.
    }
}
