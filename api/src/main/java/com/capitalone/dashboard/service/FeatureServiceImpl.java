package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.QScopeOwner;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import com.mysema.query.BooleanBuilder;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

@Service
public class FeatureServiceImpl implements FeatureService {

	private final ComponentRepository componentRepository;
	private final FeatureRepository featureRepository;
	private final CollectorRepository collectorRepository;
	private final static String NOT_EQUAL = "$ne";
	private final static String EQUAL = "$eq";

	private enum Status {
		TOTAL, DONE, InProgress
	}

	/**
	 * Default autowired constructor for repositories
	 *
	 * @param componentRepository
	 *            Repository containing components used by the UI (populated by
	 *            UI)
	 * @param collectorRepository
	 *            Repository containing all registered collectors
	 * @param featureRepository
	 *            Repository containing all features
	 */
	@Autowired
	public FeatureServiceImpl(ComponentRepository componentRepository,
			CollectorRepository collectorRepository, FeatureRepository featureRepository) {
		this.componentRepository = componentRepository;
		this.featureRepository = featureRepository;
		this.collectorRepository = collectorRepository;
	}

	/**
	 * Retrieves a single story based on a back-end story number
	 *
	 * @param componentId
	 *            The ID of the related UI component that will reference
	 *            collector item content from this collector
	 * @param storyNumber
	 *            A back-end story ID used by a source system
	 * @return A data response list of type Feature containing a single story
	 */
	@Override
	public DataResponse<List<Feature>> getStory(ObjectId componentId, String storyNumber) {
		Component component = componentRepository.findOne(componentId);
		if ((component == null) || CollectionUtils.isEmpty(component.getCollectorItems())
				|| CollectionUtils
						.isEmpty(component.getCollectorItems().get(CollectorType.ScopeOwner))
				|| (component.getCollectorItems().get(CollectorType.ScopeOwner).get(0) == null)) {
			return getEmptyDataResponse();
		}

		CollectorItem item = component.getCollectorItems().get(CollectorType.ScopeOwner).get(0);

		QScopeOwner team = new QScopeOwner("team");
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(team.collectorItemId.eq(item.getId()));

		// Get one story based on story number, based on component
		List<Feature> story = featureRepository.getStoryByNumber(storyNumber);
		Collector collector = collectorRepository.findOne(item.getCollectorId());
		return new DataResponse<>(story, collector.getLastExecuted());
	}

	private DataResponse<List<Feature>> getEmptyDataResponse() {
		Feature f = new Feature();
		List<Feature> l = new ArrayList<>();
		l.add(f);
		return new DataResponse<>(l, 0);
	}

	/**
	 * Retrieves all stories for a given team and their current sprint
	 *
	 * @param componentId
	 *            The ID of the related UI component that will reference
	 *            collector item content from this collector
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @return A data response list of type Feature containing all features for
	 *         the given team and current sprint
	 */
	@Override
	public DataResponse<List<Feature>> getRelevantStories(ObjectId componentId, String teamId,
			Optional<String> agileType) {
		Component component = componentRepository.findOne(componentId);
		if ((component == null) || CollectionUtils.isEmpty(component.getCollectorItems())
				|| CollectionUtils
						.isEmpty(component.getCollectorItems().get(CollectorType.ScopeOwner))
				|| (component.getCollectorItems().get(CollectorType.ScopeOwner).get(0) == null)) {
			return getEmptyDataResponse();
		}

		CollectorItem item = component.getCollectorItems().get(CollectorType.ScopeOwner).get(0);

		QScopeOwner team = new QScopeOwner("team");
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(team.collectorItemId.eq(item.getId()));

		// Get teamId first from available collector item, based on
		// component
		List<Feature> relevantStories = new ArrayList<Feature>();
		if (agileType.isPresent()
				&& FeatureCollectorConstants.KANBAN_SPRINT_ID.equalsIgnoreCase(agileType.get())) {
			// Kanban
			relevantStories = featureRepository.queryByOrderBySStatusDesc(teamId,
					getCurrentISODateTime(), EQUAL, FeatureCollectorConstants.KANBAN_SPRINT_ID);
		} else if (agileType.isPresent()
				&& FeatureCollectorConstants.SCRUM_SPRINT_ID.equalsIgnoreCase(agileType.get())) {
			// Scrum
			relevantStories = featureRepository.queryByOrderBySStatusDesc(teamId,
					getCurrentISODateTime(), NOT_EQUAL, FeatureCollectorConstants.KANBAN_SPRINT_ID);
		} else {
			// Legacy
			relevantStories = featureRepository.queryByOrderBySStatusDesc(teamId,
					getCurrentISODateTime());
		}

		Collector collector = collectorRepository.findOne(item.getCollectorId());

		return new DataResponse<>(relevantStories, collector.getLastExecuted());
	}

	/**
	 * Retrieves all unique super features and their total sub feature estimates
	 * for a given team and their current sprint
	 *
	 * @param componentId
	 *            The ID of the related UI component that will reference
	 *            collector item content from this collector
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @return A data response list of type Feature containing the unique
	 *         features plus their sub features' estimates associated to the
	 *         current sprint and team
	 */
	@Override
	public DataResponse<List<Feature>> getFeatureEstimates(ObjectId componentId, String teamId,
			Optional<String> agileType, Optional<String> estimateMetricType) {
		Component component = componentRepository.findOne(componentId);

		if ((component == null) || CollectionUtils.isEmpty(component.getCollectorItems())
				|| CollectionUtils
						.isEmpty(component.getCollectorItems().get(CollectorType.ScopeOwner))
				|| (component.getCollectorItems().get(CollectorType.ScopeOwner).get(0) == null)) {
			return getEmptyDataResponse();
		}

		CollectorItem item = component.getCollectorItems().get(CollectorType.ScopeOwner).get(0);
		QScopeOwner team = new QScopeOwner("team");
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(team.collectorItemId.eq(item.getId()));

		// Get teamId first from available collector item, based on component
		List<Feature> relevantFeatureEstimates = new ArrayList<Feature>();
		relevantFeatureEstimates = getRelevantFeaturesEstimates(teamId, agileType);
		// epicID : epic information (in the form of a Feature object)
		Map<String, Feature> epicIDToEpicFeatureMap = new HashMap<>();

		setFeatureEstimates(estimateMetricType, relevantFeatureEstimates, epicIDToEpicFeatureMap);

		if (isEstimateTime(estimateMetricType)) {
			// time estimate is in minutes but we want to return in hours
			for (Feature f : epicIDToEpicFeatureMap.values()) {
				f.setsEstimate(String.valueOf(Integer.valueOf(f.getsEstimate()) / 60));
			}
		}
		
		Collector collector = collectorRepository.findOne(item.getCollectorId());
		return new DataResponse<>(new ArrayList<>(epicIDToEpicFeatureMap.values()), collector.getLastExecuted());
	}

	private void setFeatureEstimates(Optional<String> estimateMetricType, List<Feature> relevantFeatureEstimates, Map<String, Feature> epicIDToEpicFeatureMap) {
		for (Feature tempRs : relevantFeatureEstimates) {
			String epicID = tempRs.getsEpicID();

			if (StringUtils.isEmpty(epicID))
				continue;

			Feature feature = epicIDToEpicFeatureMap.get(epicID);
			if (feature == null) {
				feature = new Feature();
				feature.setId(null);
				feature.setsEpicID(epicID);
				feature.setsEpicNumber(tempRs.getsEpicNumber());
				feature.setsEpicName(tempRs.getsEpicName());
				feature.setsEstimate("0");
				epicIDToEpicFeatureMap.put(epicID, feature);
			}

			// if estimateMetricType is hours accumulate time estimate in minutes for better precision ... divide by 60 later
			int estimate = getEstimate(tempRs, estimateMetricType);

			feature.setsEstimate(String.valueOf(Integer.valueOf(feature.getsEstimate()) + estimate));
		}
	}

	private List<Feature> getRelevantFeaturesEstimates(String teamId, Optional<String> agileType) {
		List<Feature> relevantFeatureEstimates;
		if (agileType.isPresent()
				&& FeatureCollectorConstants.KANBAN_SPRINT_ID.equalsIgnoreCase(agileType.get())) {
			// Kanban
			relevantFeatureEstimates = featureRepository.getInProgressFeaturesEstimatesByTeamId(
					teamId, getCurrentISODateTime(), EQUAL, FeatureCollectorConstants.KANBAN_SPRINT_ID);
		} else if (agileType.isPresent()
				&& FeatureCollectorConstants.SCRUM_SPRINT_ID.equalsIgnoreCase(agileType.get())) {
			// Scrum
			relevantFeatureEstimates = featureRepository.getInProgressFeaturesEstimatesByTeamId(
					teamId, getCurrentISODateTime(), NOT_EQUAL, FeatureCollectorConstants.KANBAN_SPRINT_ID);
		} else {
			// Legacy
			relevantFeatureEstimates = featureRepository
					.getInProgressFeaturesEstimatesByTeamId(teamId, getCurrentISODateTime());
		}
		return relevantFeatureEstimates;
	}

	private DataResponse<List<Feature>> getEstimate(ObjectId componentId, String teamId,
			Status status, Optional<String> agileType, Optional<String> estimateMetricType) {
		Component component = componentRepository.findOne(componentId);
		if ((component == null) || CollectionUtils.isEmpty(component.getCollectorItems())
				|| CollectionUtils
						.isEmpty(component.getCollectorItems().get(CollectorType.ScopeOwner))
				|| (component.getCollectorItems().get(CollectorType.ScopeOwner).get(0) == null)) {
			return getEmptyDataResponse();
		}

		CollectorItem item = component.getCollectorItems().get(CollectorType.ScopeOwner).get(0);
		QScopeOwner team = new QScopeOwner("team");
		BooleanBuilder builder = new BooleanBuilder();

		builder.and(team.collectorItemId.eq(item.getId()));

		// Get teamId first from available collector item, based on component
		List<Feature> storyEstimates;
		switch (status) {
		case TOTAL:
			storyEstimates = getFeaturesForTotal(teamId, agileType);

			break;

		case DONE:
			storyEstimates = getFeaturesForDone(teamId, agileType);

			break;

		case InProgress:
			storyEstimates = getFeaturesForInProgress(teamId, agileType);
			break;

		default:
			storyEstimates = new ArrayList<>();
			break;
		}

		List<Feature> cumulativeEstimate = new ArrayList<>();
		Feature f = new Feature();
		int lineTotalEstimate = 0;
		lineTotalEstimate = getLineTotalEstimate(estimateMetricType, storyEstimates, lineTotalEstimate);

		lineTotalEstimate = getLineTotalEstimateInHours(estimateMetricType, lineTotalEstimate);

		f.setsEstimate(Integer.toString(lineTotalEstimate));
		cumulativeEstimate.add(f);
		Collector collector = collectorRepository.findOne(item.getCollectorId());
		return new DataResponse<>(cumulativeEstimate, collector.getLastExecuted());
	}

	private int getLineTotalEstimateInHours(Optional<String> estimateMetricType, int lineTotalEstimate) {
		if (isEstimateTime(estimateMetricType)) {
			// time estimate is in minutes but we want to return in hours
			lineTotalEstimate /= 60;
		}
		return lineTotalEstimate;
	}

	private int getLineTotalEstimate(Optional<String> estimateMetricType, List<Feature> storyEstimates, int lineTotalEstimate) {
		for (Feature tempRs : storyEstimates) {
			// if estimateMetricType is hours accumulate time estimate in minutes for better precision ... divide by 60 later
			lineTotalEstimate += getEstimate(tempRs, estimateMetricType);
		}
		return lineTotalEstimate;
	}

	private List<Feature> getFeaturesForInProgress(String teamId, Optional<String> agileType) {
		List<Feature> storyEstimates;
		if (agileType.isPresent() && FeatureCollectorConstants.KANBAN_SPRINT_ID
                .equalsIgnoreCase(agileType.get())) {
            // Kanban
            storyEstimates = featureRepository.getSprintBacklogInProgress(teamId,
                    getCurrentISODateTime(), EQUAL, FeatureCollectorConstants.KANBAN_SPRINT_ID);
        } else if (agileType.isPresent() && FeatureCollectorConstants.SCRUM_SPRINT_ID
                .equalsIgnoreCase(agileType.get())) {
            // Scrum
            storyEstimates = featureRepository.getSprintBacklogInProgress(teamId,
                    getCurrentISODateTime(), NOT_EQUAL, FeatureCollectorConstants.KANBAN_SPRINT_ID);
        } else {
            // Legacy
            storyEstimates = featureRepository.getSprintBacklogInProgress(teamId,
                    getCurrentISODateTime());
        }
		return storyEstimates;
	}

	private List<Feature> getFeaturesForDone(String teamId, Optional<String> agileType) {
		List<Feature> storyEstimates;
		if (agileType.isPresent() && FeatureCollectorConstants.KANBAN_SPRINT_ID
                .equalsIgnoreCase(agileType.get())) {
            // Kanban
            storyEstimates = featureRepository.getSprintBacklogDone(teamId,
                    getCurrentISODateTime(), EQUAL, FeatureCollectorConstants.KANBAN_SPRINT_ID);
        } else if (agileType.isPresent() && FeatureCollectorConstants.SCRUM_SPRINT_ID
                .equalsIgnoreCase(agileType.get())) {
            // Scrum
            storyEstimates = featureRepository.getSprintBacklogDone(teamId,
                    getCurrentISODateTime(), NOT_EQUAL, FeatureCollectorConstants.KANBAN_SPRINT_ID);
        } else {
            // Legacy
            storyEstimates = featureRepository.getSprintBacklogDone(teamId,
                    getCurrentISODateTime());
        }
		return storyEstimates;
	}

	private List<Feature> getFeaturesForTotal(String teamId, Optional<String> agileType) {
		List<Feature> storyEstimates;
		if (agileType.isPresent() && FeatureCollectorConstants.KANBAN_SPRINT_ID
                .equalsIgnoreCase(agileType.get())) {
            // Kanban
            storyEstimates = featureRepository.getSprintBacklogTotal(teamId,
                    getCurrentISODateTime(), EQUAL, FeatureCollectorConstants.KANBAN_SPRINT_ID);
        } else if (agileType.isPresent() && FeatureCollectorConstants.SCRUM_SPRINT_ID
                .equalsIgnoreCase(agileType.get())) {
            // Scrum
            storyEstimates = featureRepository.getSprintBacklogTotal(teamId,
                    getCurrentISODateTime(), NOT_EQUAL, FeatureCollectorConstants.KANBAN_SPRINT_ID);
        } else {
            // Legacy
            storyEstimates = featureRepository.getSprintBacklogTotal(teamId,
                    getCurrentISODateTime());
        }
		return storyEstimates;
	}

	/**
	 * Retrieves estimate total of all features in the current sprint and for
	 * the current team.
	 *
	 * @param componentId
	 *            The ID of the related UI component that will reference
	 *            collector item content from this collector
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @return A data response list of type Feature containing the total
	 *         estimate number for all features
	 */
	@Override
	public DataResponse<List<Feature>> getTotalEstimate(ObjectId componentId, String teamId,
			Optional<String> agileType, Optional<String> estimateMetricType) {
		return getEstimate(componentId, teamId, Status.TOTAL, agileType, estimateMetricType);
	}

	/**
	 * Retrieves estimate in-progress of all features in the current sprint and
	 * for the current team.
	 *
	 * @param componentId
	 *            The ID of the related UI component that will reference
	 *            collector item content from this collector
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @return A data response list of type Feature containing the in-progress
	 *         estimate number for all features
	 */
	@Override
	public DataResponse<List<Feature>> getInProgressEstimate(ObjectId componentId, String teamId,
			Optional<String> agileType, Optional<String> estimateMetricType) {
		return getEstimate(componentId, teamId, Status.InProgress, agileType, estimateMetricType);
	}

	/**
	 * Retrieves estimate done of all features in the current sprint and for the
	 * current team.
	 *
	 * @param componentId
	 *            The ID of the related UI component that will reference
	 *            collector item content from this collector
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @return A data response list of type Feature containing the done estimate
	 *         number for all features
	 */
	@Override
	public DataResponse<List<Feature>> getDoneEstimate(ObjectId componentId, String teamId,
			Optional<String> agileType, Optional<String> estimateMetricType) {
		return getEstimate(componentId, teamId, Status.DONE, agileType, estimateMetricType);
	}

	/**
	 * Retrieves the current sprint's detail for a given team.
	 *
	 * @param componentId
	 *            The ID of the related UI component that will reference
	 *            collector item content from this collector
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * @return A data response list of type Feature containing several relevant
	 *         sprint fields for the current team's sprint
	 */
	@Override
	public DataResponse<List<Feature>> getCurrentSprintDetail(ObjectId componentId, String teamId,
			Optional<String> agileType) {
		Component component = componentRepository.findOne(componentId);
		if ((component == null) || CollectionUtils.isEmpty(component.getCollectorItems())
				|| CollectionUtils
						.isEmpty(component.getCollectorItems().get(CollectorType.ScopeOwner))
				|| (component.getCollectorItems().get(CollectorType.ScopeOwner).get(0) == null)) {
			return getEmptyDataResponse();
		}

		CollectorItem item = component.getCollectorItems().get(CollectorType.ScopeOwner).get(0);
		QScopeOwner team = new QScopeOwner("team");
		BooleanBuilder builder = new BooleanBuilder();

		builder.and(team.collectorItemId.eq(item.getId()));

		// Get teamId first from available collector item, based on component
		List<Feature> sprintResponse = new ArrayList<Feature>();
		if (agileType.isPresent()
				&& FeatureCollectorConstants.KANBAN_SPRINT_ID.equalsIgnoreCase(agileType.get())) {
			// Kanban
			sprintResponse = featureRepository.getCurrentSprintDetail(teamId,
					getCurrentISODateTime(), EQUAL, FeatureCollectorConstants.KANBAN_SPRINT_ID);
		} else if (agileType.isPresent()
				&& FeatureCollectorConstants.SCRUM_SPRINT_ID.equalsIgnoreCase(agileType.get())) {
			// Scrum
			sprintResponse = featureRepository.getCurrentSprintDetail(teamId,
					getCurrentISODateTime(), NOT_EQUAL, FeatureCollectorConstants.KANBAN_SPRINT_ID);
		} else {
			// Legacy
			sprintResponse = featureRepository.getCurrentSprintDetail(teamId,
					getCurrentISODateTime());
		}

		List<Feature> sprintDetail = new ArrayList<>();
		for (Feature f : sprintResponse) {
			sprintDetail.add(f);
		}
		Collector collector = collectorRepository.findOne(item.getCollectorId());
		return new DataResponse<>(sprintDetail, collector.getLastExecuted());
	}

	/**
	 * Retrieves the current system time stamp in ISO date time format. Because
	 * this is not using SimpleTimeFormat, this should be thread safe.
	 *
	 * @return A string representation of the current date time stamp in ISO
	 *         format from the current time zone
	 */
	private String getCurrentISODateTime() {
		return DatatypeConverter.printDateTime(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
	}
	
	private boolean isEstimateTime(Optional<String> estimateMetricType) {
		return estimateMetricType.isPresent() && FeatureCollectorConstants.STORY_HOURS_ESTIMATE.equalsIgnoreCase(estimateMetricType.get());
	}
	
	private int getEstimate(Feature feature, Optional<String> estimateMetricType) {
		int rt = 0;
		
		if (isEstimateTime(estimateMetricType)) {
			if (feature.getsEstimateTime() != null) {
				rt = feature.getsEstimateTime().intValue();
			}
		} else {
			// default to story points since that should be the most common use case
			if (!StringUtils.isEmpty(feature.getsEstimate())) {
				rt = Integer.parseInt(feature.getsEstimate());
			}
		}
		
		return rt;
	}
}
