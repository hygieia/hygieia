package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.SubversionRepo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.util.*;

/**
 * SubversionClient implementation that uses SVNKit to fetch information about
 * Subversion repositories.
 */
@Component
public class DefaultSubversionClient implements SubversionClient {
    private static final Log LOG = LogFactory.getLog(DefaultSubversionClient.class);

    private final SubversionSettings settings;
    @Autowired
    public DefaultSubversionClient(SubversionSettings settings) {
        this.settings = settings;
        DAVRepositoryFactory.setup();
    }

    @Override
    public List<Commit> getCommits(SubversionRepo repo, long startRevision) {
        List<Commit> commits = new ArrayList<>();

        for (Object entry : getHistory(repo.getRepoUrl(), startRevision)) {
            SVNLogEntry logEntry = (SVNLogEntry) entry;

            Commit commit = new Commit();
            commit.setTimestamp(System.currentTimeMillis());
            commit.setScmUrl(repo.getRepoUrl());
            commit.setScmRevisionNumber(String.valueOf(logEntry.getRevision()));
            commit.setScmAuthor(logEntry.getAuthor());
            commit.setScmCommitLog(logEntry.getMessage());
            commit.setScmCommitTimestamp(logEntry.getDate().getTime());
            commit.setNumberOfChanges(logEntry.getChangedPaths().size());
            commits.add(commit);
        }

        return commits;
    }

    public long getRevisionClosestTo(String url, Date revisionDate) {

        try {
            return getSvnRepository(url).getDatedRevision(revisionDate);
        } catch (SVNException svne) {
            LOG.error("Subversion repo: " + url, svne);
        }

        return 0;
    }

    @SuppressWarnings("unchecked")
    private Collection<SVNLogEntry> getHistory(String url, long startRevision) {
        long endRevision = -1; //HEAD (the latest) revision

        try {
            return getSvnRepository(url).log(new String[] {""}, null, startRevision, endRevision, true, true);
        } catch (SVNException svne) {
            LOG.error("Subversion repo: " + url, svne);
        }

        return Collections.emptySet();
    }

    private SVNRepository getSvnRepository(String url) throws SVNException {
        SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(
                settings.getUsername(), settings.getPassword());
        repository.setAuthenticationManager(authManager);
        return repository;
    }
}
