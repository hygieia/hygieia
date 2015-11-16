package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.SubversionRepo;

import java.util.Date;
import java.util.List;

/**
 * Client for fetching commit history from Subversion
 */
public interface SubversionClient {

    /**
     * Fetch all of the commits for the provided SubversionRepo.
     *
     * @param repo SubversionRepo
     * @param startRevision starting revision number
     * @return all commits in repo
     */
    List<Commit> getCommits(SubversionRepo repo, long startRevision);

    /**
     * Find the revision number closest to the supplied date.
     *
     * @param url url of subversion repository
     * @param revisionDate revision date
     * @return revision number
     */
    long getRevisionClosestTo(String url, Date revisionDate);
}
