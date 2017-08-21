package com.capitalone.dashboard.client.team;

import com.capitalone.dashboard.client.JiraClient;
import com.capitalone.dashboard.model.Team;
import com.capitalone.dashboard.repository.FeatureCollectorRepository;
import com.capitalone.dashboard.repository.TeamRepository;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import com.capitalone.dashboard.util.FeatureSettings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the primary implemented/extended data collector for the feature
 * collector. This will get data from the source system, but will grab the
 * majority of needed data and aggregate it in a single, flat MongoDB collection
 * for consumption.
 * 
 * @author kfk884
 * 
 */
public class TeamDataClientImpl implements TeamDataClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(TeamDataClientImpl.class);

	private final FeatureSettings featureSettings;
	private final TeamRepository teamRepo;
	private final FeatureCollectorRepository featureCollectorRepository;
	private final JiraClient jiraClient;

	/**
	 * Extends the constructor from the super class.
	 * 
	 * @param teamRepository
	 */
	public TeamDataClientImpl(FeatureCollectorRepository featureCollectorRepository, FeatureSettings featureSettings,
			TeamRepository teamRepository, JiraClient jiraClient) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Constructing data collection for the feature widget, team-level data...");
		}

		this.featureSettings = featureSettings;
		this.featureCollectorRepository = featureCollectorRepository;
		this.teamRepo = teamRepository;
		this.jiraClient = jiraClient;
	}
	
	/**
	 * Explicitly updates queries for the source system, and initiates the
	 * update to MongoDB from those calls.
	 */
	public int updateTeamInformation() {
		int count = 0;

		List<Team> teams = null;
		String jiraBoardAsTeam = featureSettings.getJiraBoardAsTeam();
		if (!StringUtils.isEmpty(jiraBoardAsTeam) && Boolean.parseBoolean(jiraBoardAsTeam)) {
			teams = jiraClient.getBoards(0, new ArrayList<Team>());
		} else {
			teams = jiraClient.getTeams();
		}
		
		if (CollectionUtils.isNotEmpty(teams)) {
			updateMongoInfo(teams);
			count += teams.size();
		}
		
		return count;
	}

	/**
	 * Updates the MongoDB with a JSONArray received from the source system
	 * back-end with story-based data.
	 * 
	 * @param jiraTeams
	 *            A list response of Jira teams from the source system
	 */
	private void updateMongoInfo(List<Team> jiraTeams) {
		ObjectId jiraCollectorId = featureCollectorRepository.findByName(FeatureCollectorConstants.JIRA).getId();
		
		for (Team jiraTeam : jiraTeams) {
			String teamId = jiraTeam.getTeamId();
			
			/*
			 * Initialize DOMs
			 */
			Team team = teamRepo.findByTeamId(teamId);

			if (team == null) {
				team = new Team("", "");
			}

			// collectorId
			team.setCollectorId(jiraCollectorId);

			// teamId
			team.setTeamId(teamId);

			// name
			team.setName(jiraTeam.getName());

			// teamType
			team.setTeamType(jiraTeam.getTeamType());

			// changeDate - does not exist for jira
			team.setChangeDate("");

			// assetState - does not exist for jira
			team.setAssetState("Active");

			// isDeleted - does not exist for jira
			team.setIsDeleted("False");

			// Saving back to MongoDB
			teamRepo.save(team);
		}
	}

	/**
	 * Retrieves the maximum change date for a given query.
	 *
	 * @return A list object of the maximum change date
	 */
	public String getMaxChangeDate() {
		String data = null;

		try {
			List<Team> response = teamRepo.findTopByChangeDateDesc(
					featureCollectorRepository.findByName(FeatureCollectorConstants.JIRA).getId(),
					featureSettings.getDeltaCollectorItemStartDate());
			if ((response != null) && !response.isEmpty()) {
				data = response.get(0).getChangeDate();
			}
		} catch (Exception e) {
			LOGGER.error("There was a problem retrieving or parsing data from the local "
					+ "repository while retrieving a max change date\nReturning null");
		}

		return data;
	}

}