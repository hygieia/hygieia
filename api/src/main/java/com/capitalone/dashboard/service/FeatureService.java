package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.SprintEstimate;
import com.capitalone.dashboard.model.DataResponse;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;

public interface FeatureService {

	/**
	 * Retrieves all stories for a given team and their current sprint
	 * 
	 * @param componentId
	 *            The ID of the related UI component that will reference
	 *            collector item content from this collector
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @param agileType
	 * 			  Agile type to be retrieved (e.g., kanban | scrum)
	 * 
	 * @return A data response list of type Feature containing all features for
	 *         the given team and current sprint
	 */
	DataResponse<List<Feature>> getRelevantStories(ObjectId componentId,
			String teamId, Optional<String> agileType);

	/**
	 * Retrieves a single story based on a back-end story number
	 * 
	 * @param componentId
	 *            The ID of the related UI component that will reference
	 *            collector item content from this collector
	 * @param storyNumber
	 *            A back-end story ID used by a source system
	 * 
	 * @return A data response list of type Feature containing a single story
	 */
	DataResponse<List<Feature>> getStory(ObjectId componentId,
			String storyNumber);

	/**
	 * Retrieves all unique super features and their total sub feature estimates
	 * for a given team and their current sprint
	 * 
	 * @param componentId
	 *            The ID of the related UI component that will reference
	 *            collector item content from this collector
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @param agileType
	 * 			  Agile type to be retrieved (e.g., kanban | scrum)
	 * @param estimateMetricType
	 * 			  The reporting metric (hours | storypoints)
	 * 
	 * @return A data response list of type Feature containing the unique
	 *         features plus their sub features' estimates associated to the
	 *         current sprint and team
	 */
	DataResponse<List<Feature>> getFeatureEpicEstimates(ObjectId componentId,
			String teamId, Optional<String> agileType, Optional<String> estimateMetricType);
	
	/**
	 * Retrieves estimate total of all features in the current sprint and for
	 * the current team.
	 * 
	 * @param componentId
	 *            The ID of the related UI component that will reference
	 *            collector item content from this collector
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @param agileType
	 * 			  Agile type to be retrieved (e.g., kanban | scrum)
	 * @param estimateMetricType
	 * 			  The reporting metric (hours | storypoints)
	 * 
	 * @return A data response list of type Feature containing the total
	 *         estimate number for all features
	 */
	@Deprecated
	DataResponse<List<Feature>> getTotalEstimate(ObjectId componentId,
			String teamId, Optional<String> agileType, Optional<String> estimateMetricType);
	
	/**
	 * Retrieves estimate in-progress of all features in the current sprint and
	 * for the current team.
	 * 
	 * @param componentId
	 *            The ID of the related UI component that will reference
	 *            collector item content from this collector
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @param agileType
	 * 			  Agile type to be retrieved (e.g., kanban | scrum)
	 * @param estimateMetricType
	 * 			  The reporting metric (hours | storypoints)
	 * 
	 * @return A data response list of type Feature containing the in-progress
	 *         estimate number for all features
	 */
	@Deprecated
	DataResponse<List<Feature>> getInProgressEstimate(ObjectId componentId,
			String teamId, Optional<String> agileType, Optional<String> estimateMetricType);

	/**
	 * Retrieves estimate done of all features in the current sprint and for the
	 * current team.
	 * 
	 * @param componentId
	 *            The ID of the related UI component that will reference
	 *            collector item content from this collector
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @param agileType
	 * 			  Agile type to be retrieved (e.g., kanban | scrum)
	 * @param estimateMetricType
	 * 			  The reporting metric (hours | storypoints)
	 * 
	 * @return A data response list of type Feature containing the done estimate
	 *         number for all features
	 */
	@Deprecated
	DataResponse<List<Feature>> getDoneEstimate(ObjectId componentId,
			String teamId, Optional<String> agileType, Optional<String> estimateMetricType);

	/**
	 * Retrieves estimate done of all features in the current sprint(s) for the current team
	 * 
	 * @param componentId
	 *            The ID of the related UI component that will reference
	 *            collector item content from this collector
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @param agileType
	 * 			  Agile type to be retrieved (e.g., kanban | scrum)
	 * @param estimateMetricType
	 * 			  The reporting metric (hours | storypoints)
	 * 
	 * @return A data response list of type Feature containing the done estimate
	 *         number for all features
	 */
	DataResponse<SprintEstimate> getAggregatedSprintEstimates(ObjectId componentId,
			String teamId, Optional<String> agileType, Optional<String> estimateMetricType);
	
	/**
	 * Retrieves the current sprint's detail for a given team.
	 * 
	 * @param componentId
	 *            The ID of the related UI component that will reference
	 *            collector item content from this collector
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @param agileType
	 * 			  Agile type to be retrieved (e.g., kanban | scrum)
	 * 
	 * @return A data response list of type Feature containing several relevant
	 *         sprint fields for the current team's sprint
	 */
	DataResponse<List<Feature>> getCurrentSprintDetail(ObjectId componentId,
			String teamId, Optional<String> agileType);
}
