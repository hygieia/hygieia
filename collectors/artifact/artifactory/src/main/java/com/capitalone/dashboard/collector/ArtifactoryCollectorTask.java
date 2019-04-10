package com.capitalone.dashboard.collector;


import com.capitalone.dashboard.model.ArtifactItem;
import com.capitalone.dashboard.model.ArtifactoryCollector;
import com.capitalone.dashboard.model.ArtifactoryRepo;
import com.capitalone.dashboard.model.BaseArtifact;
import com.capitalone.dashboard.model.BinaryArtifact;
import com.capitalone.dashboard.model.RepoAndPattern;
import com.capitalone.dashboard.repository.ArtifactItemRepository;
import com.capitalone.dashboard.repository.ArtifactoryCollectorRepository;
import com.capitalone.dashboard.repository.ArtifactoryRepoRepository;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.BinaryArtifactRepository;
import com.google.common.collect.Iterables;
import org.apache.commons.collections.map.HashedMap;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ArtifactoryCollectorTask extends CollectorTask<ArtifactoryCollector>{
	private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactoryCollectorTask.class);

	private final ArtifactoryCollectorRepository artifactoryCollectorRepository;
	private final ArtifactoryRepoRepository artifactoryRepoRepository;
	private final ArtifactItemRepository artifactItemRepository;
	private final ArtifactoryClient artifactoryClient;
	private final ArtifactorySettings artifactorySettings;
	private final BinaryArtifactRepository binaryArtifactRepository;

	@Autowired
	public ArtifactoryCollectorTask(TaskScheduler taskScheduler,
									ArtifactoryCollectorRepository artifactoryCollectorRepository,
									ArtifactoryRepoRepository artifactoryRepoRepository,
									ArtifactItemRepository artifactItemRepository, BinaryArtifactRepository binaryArtifactRepository,
									ArtifactoryClient artifactoryClient,
									ArtifactorySettings artifactorySettings) {
		super(taskScheduler, "Artifactory");
		this.artifactoryCollectorRepository = artifactoryCollectorRepository;
		this.artifactoryRepoRepository = artifactoryRepoRepository;
		this.artifactItemRepository = artifactItemRepository;
		this.binaryArtifactRepository = binaryArtifactRepository;
		this.artifactoryClient = artifactoryClient;
		this.artifactorySettings = artifactorySettings;
	}

	@Override
	public ArtifactoryCollector getCollector() {
		return ArtifactoryCollector.prototype(artifactorySettings);
	}

	@Override
	public BaseCollectorRepository<ArtifactoryCollector> getCollectorRepository() {
		return artifactoryCollectorRepository;
	}

	@Override
	public String getCron() {
		return artifactorySettings.getCron();
	}

	@Override
	public void collect(ArtifactoryCollector collector) {
		switch (artifactorySettings.getMode()) {
			case REPO_BASED:
				collectRepoBased(collector);
				break;
			case ARTIFACT_BASED:
				collectArtifactBased(collector);
				break;
			default:
				LOGGER.error("Error with collection mode. Valid modes are REPO_BASED or ARTIFACT_BASED to be set as properties.");
				break;
		}

	}


	protected void collectRepoBased(ArtifactoryCollector collector) {
		Set<ObjectId> udId = new HashSet<>();
		udId.add(collector.getId());
		List<ArtifactoryRepo> existingRepos = artifactoryRepoRepository.findByCollectorIdIn(udId);
		List<ArtifactoryRepo> activeRepos = new ArrayList<>();
		clean(collector, existingRepos);
		List<String> instanceUrls = collector.getArtifactoryServers();
		instanceUrls.forEach(instanceUrl -> {
			long start = System.currentTimeMillis();
			logBanner(instanceUrl);
			if (instanceUrl.lastIndexOf('/') == instanceUrl.length()-1) {
				List<ArtifactoryRepo> repos = artifactoryClient.getRepos(instanceUrl);
				log("Fetched repos", start);
				activeRepos.addAll(repos);
				addNewRepos(repos, existingRepos, collector);
				addNewArtifacts(enabledRepos(collector, instanceUrl));
			} else {
				LOGGER.error("Error with artifactory url: " + instanceUrl + ". Url does not end with '/'");
			}
			log("Finished", start);
		});
	}


	protected void collectArtifactBased(ArtifactoryCollector collector) {
		Set<ObjectId> udId = new HashSet<>();
		udId.add(collector.getId());
		List<ArtifactItem> existingItems = artifactItemRepository.findByCollectorIdIn(udId);

		List<String> instanceUrls = collector.getArtifactoryServers();
		instanceUrls.forEach(instanceUrl -> {
			long start = System.currentTimeMillis();
			logBanner(instanceUrl);
			if (instanceUrl.lastIndexOf('/') == instanceUrl.length() - 1) {
				getRepos().forEach(repo->{
					long lastUpdated = getLastUpdated(existingItems);
					String pattern = (getPatterns().get(repo)).get(0);
                    List<BaseArtifact> baseArtifacts = artifactoryClient.getArtifactItems(instanceUrl,repo,pattern,lastUpdated);
                    log("Collecting repository ====>>> "+ repo);
					addNewArtifactsItems(baseArtifacts,existingItems,collector);
                });
                log("Fetched repos", start,getRepos().size());
			} else {
				LOGGER.error("Error with artifactory url: " + instanceUrl + ". Url does not end with '/'");
			}
			log("Finished", start);
		});
	}


	/**
	 * Clean up unused artifactory collector items
	 *
	 * @param collector the {@link ArtifactoryCollector}
	 */
	private void clean(ArtifactoryCollector collector, List<ArtifactoryRepo> existingRepos) {
		// find the server url's to collect from
		Set<String> serversToBeCollected = new HashSet<>();
		serversToBeCollected.addAll(collector.getArtifactoryServers());

		// find the repos to collect from each server url above
		List<Set<String>> repoNamesToBeCollected = new ArrayList<Set<String>>();
		List<String[]> allRepos = new ArrayList<>();
		artifactorySettings.getServers().forEach(serverSetting -> {
			allRepos.add((String[]) getRepoAndPatternsForServ(serverSetting.getRepoAndPatterns()).keySet().toArray());
		});
		for (int i = 0; i < allRepos.size(); i++) {
			Set<String> reposSet = new HashSet<>();
			if (allRepos.get(i) != null) {
				reposSet.addAll(Arrays.asList(allRepos.get(i)));
			}
			repoNamesToBeCollected.add(reposSet);
		}

		assert (serversToBeCollected.size() == repoNamesToBeCollected.size());

		List<ArtifactoryRepo> stateChangeRepoList = new ArrayList<>();
		for (ArtifactoryRepo repo : existingRepos) {
			if (isRepoEnabledAndNotCollected(collector, serversToBeCollected, repoNamesToBeCollected, repo) ||  // if it was enabled but not to be collected
					isRepoDisabledAndToBeCollected(collector, serversToBeCollected, repoNamesToBeCollected, repo)) { // OR it was disabled and now is to be collected
				repo.setEnabled(isRepoCollected(collector, serversToBeCollected, repoNamesToBeCollected, repo));
				stateChangeRepoList.add(repo);
			}
		}
		if (!CollectionUtils.isEmpty(stateChangeRepoList)) {
			artifactoryRepoRepository.save(stateChangeRepoList);
		}
	}

	private boolean isRepoCollected(ArtifactoryCollector collector, Set<String> serversToBeCollected, List<Set<String>> repoNamesToBeCollected, ArtifactoryRepo repo) {
		return collector.getId().equals(repo.getCollectorId())
				&& serversToBeCollected.contains(repo.getInstanceUrl())
				&& repoNamesToBeCollected.get(collector.getArtifactoryServers().indexOf(repo.getInstanceUrl())).contains(repo.getRepoName());
	}

	private boolean isRepoDisabledAndToBeCollected(ArtifactoryCollector collector, Set<String> serversToBeCollected, List<Set<String>> repoNamesToBeCollected, ArtifactoryRepo repo) {
		return !repo.isEnabled() && (isRepoCollected(collector, serversToBeCollected, repoNamesToBeCollected, repo));
	}

	private boolean isRepoEnabledAndNotCollected(ArtifactoryCollector collector, Set<String> serversToBeCollected, List<Set<String>> repoNamesToBeCollected, ArtifactoryRepo repo) {
		return repo.isEnabled() && (!collector.getId().equals(repo.getCollectorId())
				|| !serversToBeCollected.contains(repo.getInstanceUrl())
				|| !repoNamesToBeCollected.get(collector.getArtifactoryServers().indexOf(repo.getInstanceUrl())).contains(repo.getRepoName()));
	}


	/**
	 * Add any new {@link ArtifactoryRepo}s.
	 *
	 * @param repos					list of {@link ArtifactoryRepo}s
	 * @param existingRepos			list of existing {@link ArtifactoryRepo}s
	 * @param collector    			the {@link ArtifactoryCollector}
	 */
	private void addNewRepos(List<ArtifactoryRepo> repos, List<ArtifactoryRepo> existingRepos, ArtifactoryCollector collector) {
		long start = System.currentTimeMillis();
		int count = 0;

        List<ArtifactoryRepo> newRepos = new ArrayList<>();
        for (ArtifactoryRepo repo : repos) {
        	ArtifactoryRepo existing = null;
            if (!CollectionUtils.isEmpty(existingRepos) && (existingRepos.contains(repo))) {
                existing = existingRepos.get(existingRepos.indexOf(repo));
            }

            if (existing == null) {
                repo.setCollectorId(collector.getId());
                repo.setEnabled(false); // Do not enable for collection. Will be enabled later
                repo.setDescription(repo.getRepoName());
                newRepos.add(repo);
                count++;
            }
        }
        //save all in one shot
        if (!CollectionUtils.isEmpty(newRepos)) {
        	artifactoryRepoRepository.save(newRepos);
        }
        log("New repos", start, count);
    }

    /**
     * Add any new {@link BinaryArtifact}s
     *
     * @param enabledRepos			list of enabled {@link ArtifactoryRepo}s
     */
    private void addNewArtifacts(List<ArtifactoryRepo> enabledRepos) {
		long start = System.currentTimeMillis();

		int count = 0;
		for (ArtifactoryRepo repo : enabledRepos) {
			for (BinaryArtifact artifact : nullSafe(artifactoryClient.getArtifacts(repo.getInstanceUrl(), repo.getRepoName(), repo.getLastUpdated()))) {
				if (artifact != null && isNewArtifact(repo, artifact)) {
					artifact.setCollectorItemId(repo.getId());
					binaryArtifactRepository.save(artifact);
					count++;
				}
			}
		}

		// Iterate through list of repos and update the lastUpdated timestamp
		for (ArtifactoryRepo repo : enabledRepos) {
			repo.setLastUpdated(start);
		}
		// We set the last update time so need to save it
		artifactoryRepoRepository.save(enabledRepos);
		log("New artifacts", start, count);
	}


	/**
	 * Add any new {@link ArtifactItem}s
	 *
	 */

       private void addNewArtifactsItems(List<BaseArtifact> baseArtifacts, List<ArtifactItem> existingArtifactItems,ArtifactoryCollector collector) {
        long start = System.currentTimeMillis();
        List<BinaryArtifact> binaryArtifacts = new ArrayList<>();
        int count = 0;
        Set<ArtifactItem> existingSet = new HashSet<>(existingArtifactItems);
        for (BaseArtifact baseArtifact : baseArtifacts) {
        	ArtifactItem newArtifactItem = baseArtifact.getArtifactItem();
        	if(newArtifactItem!=null && !existingSet.contains(newArtifactItem)){
					newArtifactItem.setLastUpdated(System.currentTimeMillis());
					newArtifactItem.setCollectorId(collector.getId());
					newArtifactItem.setEnabled(true);
					newArtifactItem = artifactItemRepository.save(newArtifactItem);
					existingSet.add(newArtifactItem);
					BinaryArtifact binaryArtifact = baseArtifact.getBinaryArtifact();
					if(binaryArtifact!=null){
						binaryArtifact.setCollectorItemId(newArtifactItem.getId());
						binaryArtifacts.add(binaryArtifact);
					}
					count++;
			}
        }
        if(!binaryArtifacts.isEmpty()){
			binaryArtifacts.forEach(binaryArtifact->binaryArtifactRepository.save(binaryArtifact));
   	}
        log("New artifacts", start, count);
    }

    private List<BinaryArtifact> nullSafe(List<BinaryArtifact> builds) {
        return builds == null ? new ArrayList<BinaryArtifact>() : builds;
    }


    private List<ArtifactoryRepo> enabledRepos(ArtifactoryCollector collector, String instanceUrl) {
		return artifactoryRepoRepository.findEnabledArtifactoryRepos(collector.getId(), instanceUrl);
	}

    private boolean isNewArtifact(ArtifactoryRepo repo, BinaryArtifact artifact) {
        return Iterables.size(binaryArtifactRepository.findByAttributes(repo.getId(),
        		artifact.getArtifactGroupId(), artifact.getArtifactModule(), artifact.getArtifactVersion(), artifact.getArtifactName(),
        		artifact.getArtifactClassifier(), artifact.getArtifactExtension())) == 0;
    }

	private List<String> getRepos() {
       	List<String> repos = new ArrayList<>();
		artifactorySettings.getServers().forEach(serverSetting -> {
			repos.addAll(getRepoAndPatternsForServ(serverSetting.getRepoAndPatterns()).keySet());
		});
  		return  repos;
	}

	private Map<String,List<String>> getPatterns() {
		Map<String,List<String>> patterns = new HashedMap();
		artifactorySettings.getServers().forEach(serverSetting -> {
			patterns.putAll(getRepoAndPatternsForServ(serverSetting.getRepoAndPatterns()));
		});
		return patterns;
	}

	private static Map<String,List<String>> getRepoAndPatternsForServ(List<RepoAndPattern> repoAndPatterns){
		return repoAndPatterns.stream().collect(Collectors.toMap(RepoAndPattern::getRepo,RepoAndPattern::getPatterns ));
	}

	private long getLastUpdated(List<ArtifactItem> existingItems) {
		long lastUpdated=0;
		if(!CollectionUtils.isEmpty(existingItems)) {
			lastUpdated = existingItems.get(0).getLastUpdated();
		}else if(lastUpdated==0){
			lastUpdated = System.currentTimeMillis() - artifactorySettings.getOffSet();
		}
		return lastUpdated;
	}

}
