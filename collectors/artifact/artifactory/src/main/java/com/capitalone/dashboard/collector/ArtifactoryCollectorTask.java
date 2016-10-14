package com.capitalone.dashboard.collector;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.capitalone.dashboard.model.ArtifactoryCollector;
import com.capitalone.dashboard.model.ArtifactoryRepo;
import com.capitalone.dashboard.model.BinaryArtifact;
import com.capitalone.dashboard.repository.ArtifactoryCollectorRepository;
import com.capitalone.dashboard.repository.ArtifactoryRepoRepository;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.BinaryArtifactRepository;
import com.google.common.collect.Iterables;

@Component
public class ArtifactoryCollectorTask extends CollectorTask<ArtifactoryCollector>{
	private final ArtifactoryCollectorRepository artifactoryCollectorRepository;
	private final ArtifactoryRepoRepository artifactoryRepoRepository;
	private final ArtifactoryClient artifactoryClient;
	private final ArtifactorySettings artifactorySettings;
	private final BinaryArtifactRepository binaryArtifactRepository;
	
	@Autowired
	public ArtifactoryCollectorTask(TaskScheduler taskScheduler,
									ArtifactoryCollectorRepository artifactoryCollectorRepository,
									ArtifactoryRepoRepository artifactoryRepoRepository,
									BinaryArtifactRepository binaryArtifactRepository,
									ArtifactoryClient artifactoryClient,
									ArtifactorySettings artifactorySettings) {
		super(taskScheduler, "Artifactory");
		this.artifactoryCollectorRepository = artifactoryCollectorRepository;
		this.artifactoryRepoRepository = artifactoryRepoRepository;
		this.binaryArtifactRepository = binaryArtifactRepository;
		this.artifactoryClient = artifactoryClient;
		this.artifactorySettings = artifactorySettings;
	}
	
    @Override
    public ArtifactoryCollector getCollector() {
        return ArtifactoryCollector.prototype(artifactorySettings.getServers(), artifactorySettings.getArtifactoryEndpoints());
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
    	Set<ObjectId> udId = new HashSet<>();
        udId.add(collector.getId());
        List<ArtifactoryRepo> existingRepos = artifactoryRepoRepository.findByCollectorIdIn(udId);
        List<ArtifactoryRepo> activeRepos = new ArrayList<>();
        List<String> activeServers = new ArrayList<>();
        activeServers.addAll(collector.getArtifactoryServers());
        
        clean(collector, existingRepos);
        
        List<String> instanceUrls = collector.getArtifactoryServers();
        List<String> queryEndpoints = collector.getArtifactoryEndpoints();
    	for (int i = 0; i < instanceUrls.size(); i++) { 
    		long start = System.currentTimeMillis();
    		
    		logBanner(instanceUrls.get(i));
    		
    		List<ArtifactoryRepo> repos = artifactoryClient.getRepos(instanceUrls.get(i), (i < queryEndpoints.size() ? queryEndpoints.get(i) : ""));
    		log("Fetched repos", start);
    		activeRepos.addAll(repos);
    		addNewRepos(repos, existingRepos, collector);
    		addNewArtifacts(enabledRepos(collector, instanceUrls.get(i)));
    		
    		log("Finished", start);
    	}
    	
    	// Delete repos that will be no longer collected
    	deleteUnwantedRepos(activeRepos, existingRepos, activeServers, collector);
    }
    
    /**
     * Clean up unused artifactory collector items
     *
     * @param collector the {@link ArtifactoryCollector}
     */
    private void clean(ArtifactoryCollector collector, List<ArtifactoryRepo> existingRepos) {
    	Set<String> repoNamesToBeCollected = new HashSet<>();
        repoNamesToBeCollected.addAll(artifactorySettings.getRepos());
        List<ArtifactoryRepo> stateChangeRepoList = new ArrayList<>();
        for (ArtifactoryRepo repo : existingRepos) {
            if ((repo.isEnabled() && (!collector.getId().equals(repo.getCollectorId()) || !repoNamesToBeCollected.contains(repo.getRepoName()))) ||  // if it was enabled but not to be collected
                    (!repo.isEnabled() && (collector.getId().equals(repo.getCollectorId()) && repoNamesToBeCollected.contains(repo.getRepoName())))) { // OR it was disabled and now is to be collected
                repo.setEnabled((collector.getId().equals(repo.getCollectorId()) && repoNamesToBeCollected.contains(repo.getRepoName())));
                stateChangeRepoList.add(repo);
            }
        }
        if (!CollectionUtils.isEmpty(stateChangeRepoList)) {
        	artifactoryRepoRepository.save(stateChangeRepoList);
        }
    }
    
    private void deleteUnwantedRepos(List<ArtifactoryRepo> activeRepos, List<ArtifactoryRepo> existingRepos, List<String> activeServers, ArtifactoryCollector collector) {
    	List<ArtifactoryRepo> deleteList = new ArrayList<>();
    	
    	for (ArtifactoryRepo repo : existingRepos) {
            // if we have a collector item for the repo in repository but it's server is not what we collect, remove it.
            if (!collector.getArtifactoryServers().contains(repo.getInstanceUrl())) {
            	deleteList.add(repo);
            }

            //if the collector id of the collector item for the repo does not match with the collector ID, delete it.
            if (!repo.getCollectorId().equals(collector.getId())) {
            	deleteList.add(repo);
            }

            // this is to handle repos that have been deleted from servers. Will get 404 if we don't delete them.
            if (activeServers.contains(repo.getInstanceUrl()) && !activeRepos.contains(repo)) {
            	deleteList.add(repo);
            }

        }
        if (!CollectionUtils.isEmpty(deleteList)) {
        	artifactoryRepoRepository.delete(deleteList);
        }
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
    
    private List<BinaryArtifact> nullSafe(List<BinaryArtifact> builds) {
        return builds == null ? new ArrayList<BinaryArtifact>() : builds;
    }
    
    private List<ArtifactoryRepo> enabledRepos(ArtifactoryCollector collector, String instanceUrl) {
		return artifactoryRepoRepository.findEnabledArtifactoryRepos(collector.getId(), instanceUrl);
	}
    
    private boolean isNewArtifact(ArtifactoryRepo repo, BinaryArtifact artifact) {
        return Iterables.size(binaryArtifactRepository.findByCollectorItemIdAndArtifactGroupIdAndArtifactNameAndArtifactVersion(repo.getId(),
        		artifact.getArtifactGroupId(), artifact.getArtifactName(), artifact.getArtifactVersion())) == 0;
    }
}
