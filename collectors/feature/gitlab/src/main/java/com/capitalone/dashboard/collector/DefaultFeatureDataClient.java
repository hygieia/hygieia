package com.capitalone.dashboard.collector;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.model.GitlabTeam;
import com.capitalone.dashboard.model.ScopeOwnerCollectorItem;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.ScopeOwnerRepository;
import com.capitalone.dashboard.util.FeatureCollectorConstants;

@Component
public class DefaultFeatureDataClient implements FeatureDataClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFeatureDataClient.class);
	
	private final FeatureCollectorRepository featureRepo;
	private final ScopeOwnerRepository teamRepo;
	
	@Autowired
	public DefaultFeatureDataClient(FeatureCollectorRepository featureRepo, ScopeOwnerRepository teamRepo) {
		this.featureRepo = featureRepo;
		this.teamRepo = teamRepo;
	}

	@Override
	public void updateTeams(GitlabTeam[] gitlabTeams) {
		ObjectId gitlabFeatureCollectorId = featureRepo.findByName(FeatureCollectorConstants.GITLAB).getId();
		
		List<ScopeOwnerCollectorItem> currentTeams = new ArrayList<>();
		for(GitlabTeam gitlabTeam : gitlabTeams) {
			String teamId = String.valueOf(gitlabTeam.getId());
			
			ScopeOwnerCollectorItem team = findExistingTeam(teamId);
			
			team.setCollectorId(gitlabFeatureCollectorId);
			team.setTeamId(teamId);
			team.setName(gitlabTeam.getName());
			team.setChangeDate("");
			team.setAssetState("Active");
			team.setIsDeleted("False");
			
			currentTeams.add(team);
		}
		
		teamRepo.save(currentTeams);
		
	}

	private ScopeOwnerCollectorItem findExistingTeam(String teamId) {
		List<ScopeOwnerCollectorItem> savedTeams = teamRepo.getTeamIdById(teamId);

		// Not sure of the state of the data
		if (savedTeams.size() > 1) {
			LOGGER.warn("More than one collector item found for teamId " + teamId);
		}

		if (!savedTeams.isEmpty()) {
			return savedTeams.get(0);
		}

		return new ScopeOwnerCollectorItem();
	}

}
