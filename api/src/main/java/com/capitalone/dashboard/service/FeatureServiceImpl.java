package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.model.QScopeOwner;
import com.capitalone.dashboard.model.SprintEstimate;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

/**
 * The feature service.
 * <p>
 * Features can currently belong to 2 sprint types: scrum and kanban. In order to be considered part of the sprint
 * the feature must not be deleted and must have an "active" sprint asset state if the sprint is set. The following
 * logic also applies:
 * <p>
 * A feature is part of a scrum sprint if any of the following are true:
 * <ol>
 * <li>the feature has a sprint set that has start <= now <= end and end < EOT (9999-12-31T59:59:59.999999)</li>
 * </ol>
 * <p>
 * A feature is part of a kanban sprint if any of the following are true:
 * <ol>
 * <li>the feature does not have a sprint set</li>
 * <li>the feature has a sprint set that does not have an end date</li>
 * <li>the feature has a sprint set that has an end date >= EOT (9999-12-31T59:59:59.999999)</li>
 * </ol>
 */
@Service
public class FeatureServiceImpl implements FeatureService {

	private final ComponentRepository componentRepository;
	private final FeatureRepository featureRepository;
	private final CollectorRepository collectorRepository;

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
			return getEmptyLegacyDataResponse();
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
			return getEmptyLegacyDataResponse();
		}

		CollectorItem item = component.getCollectorItems().get(CollectorType.ScopeOwner).get(0);

		QScopeOwner team = new QScopeOwner("team");
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(team.collectorItemId.eq(item.getId()));

		// Get teamId first from available collector item, based on component
		List<Feature> relevantStories = getFeaturesForCurrentSprints(teamId, agileType.isPresent()? agileType.get() : null, false);

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
	public DataResponse<List<Feature>> getFeatureEpicEstimates(ObjectId componentId, String teamId,
			Optional<String> agileType, Optional<String> estimateMetricType) {
		Component component = componentRepository.findOne(componentId);

		if ((component == null) || CollectionUtils.isEmpty(component.getCollectorItems())
				|| CollectionUtils
						.isEmpty(component.getCollectorItems().get(CollectorType.ScopeOwner))
				|| (component.getCollectorItems().get(CollectorType.ScopeOwner).get(0) == null)) {
			return getEmptyLegacyDataResponse();
		}

		CollectorItem item = component.getCollectorItems().get(CollectorType.ScopeOwner).get(0);
		
		List<Feature> relevantFeatureEstimates = getFeaturesForCurrentSprints(teamId, agileType.isPresent()? agileType.get() : null, true);
		
		// epicID : epic information (in the form of a Feature object)
		Map<String, Feature> epicIDToEpicFeatureMap = new HashMap<>();
		
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
		
		if (isEstimateTime(estimateMetricType)) {
			// time estimate is in minutes but we want to return in hours
			for (Feature f : epicIDToEpicFeatureMap.values()) {
				f.setsEstimate(String.valueOf(Integer.valueOf(f.getsEstimate()) / 60));
			}
		}
		
		Collector collector = collectorRepository.findOne(item.getCollectorId());
		return new DataResponse<>(new ArrayList<>(epicIDToEpicFeatureMap.values()), collector.getLastExecuted());
	}
	
	@Override
	public DataResponse<SprintEstimate> getAggregatedSprintEstimates(ObjectId componentId,
			String teamId, Optional<String> agileType, Optional<String> estimateMetricType) {
		Component component = componentRepository.findOne(componentId);
		if ((component == null) || CollectionUtils.isEmpty(component.getCollectorItems())
				|| CollectionUtils
						.isEmpty(component.getCollectorItems().get(CollectorType.ScopeOwner))
				|| (component.getCollectorItems().get(CollectorType.ScopeOwner).get(0) == null)) {
			return new DataResponse<SprintEstimate>(new SprintEstimate(), 0);
		}

		CollectorItem item = component.getCollectorItems().get(CollectorType.ScopeOwner).get(0);
		Collector collector = collectorRepository.findOne(item.getCollectorId());
		
		SprintEstimate estimate = getSprintEstimates(teamId, agileType, estimateMetricType);
		return new DataResponse<>(estimate, collector.getLastExecuted());
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
	@Deprecated 
	public DataResponse<List<Feature>> getTotalEstimate(ObjectId componentId, String teamId,
			Optional<String> agileType, Optional<String> estimateMetricType) {
		Component component = componentRepository.findOne(componentId);

		if ((component == null) || CollectionUtils.isEmpty(component.getCollectorItems())
				|| CollectionUtils
						.isEmpty(component.getCollectorItems().get(CollectorType.ScopeOwner))
				|| (component.getCollectorItems().get(CollectorType.ScopeOwner).get(0) == null)) {
			return getEmptyLegacyDataResponse();
		}

		CollectorItem item = component.getCollectorItems().get(CollectorType.ScopeOwner).get(0);
		
		SprintEstimate estimate = getSprintEstimates(teamId, agileType, estimateMetricType);
		
		List<Feature> list = Collections.singletonList(new Feature());
		list.get(0).setsEstimate(Integer.toString(estimate.getTotalEstimate()));
		
		Collector collector = collectorRepository.findOne(item.getCollectorId());
		return new DataResponse<>(list, collector.getLastExecuted());
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
	@Deprecated
	public DataResponse<List<Feature>> getInProgressEstimate(ObjectId componentId, String teamId,
			Optional<String> agileType, Optional<String> estimateMetricType) {
		Component component = componentRepository.findOne(componentId);

		if ((component == null) || CollectionUtils.isEmpty(component.getCollectorItems())
				|| CollectionUtils
						.isEmpty(component.getCollectorItems().get(CollectorType.ScopeOwner))
				|| (component.getCollectorItems().get(CollectorType.ScopeOwner).get(0) == null)) {
			return getEmptyLegacyDataResponse();
		}

		CollectorItem item = component.getCollectorItems().get(CollectorType.ScopeOwner).get(0);
		
		SprintEstimate estimate = getSprintEstimates(teamId, agileType, estimateMetricType);
		
		List<Feature> list = Collections.singletonList(new Feature());
		list.get(0).setsEstimate(Integer.toString(estimate.getInProgressEstimate()));
		
		Collector collector = collectorRepository.findOne(item.getCollectorId());
		return new DataResponse<>(list, collector.getLastExecuted());
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
	@Deprecated
	public DataResponse<List<Feature>> getDoneEstimate(ObjectId componentId, String teamId,
			Optional<String> agileType, Optional<String> estimateMetricType) {
		Component component = componentRepository.findOne(componentId);

		if ((component == null) || CollectionUtils.isEmpty(component.getCollectorItems())
				|| CollectionUtils
						.isEmpty(component.getCollectorItems().get(CollectorType.ScopeOwner))
				|| (component.getCollectorItems().get(CollectorType.ScopeOwner).get(0) == null)) {
			return getEmptyLegacyDataResponse();
		}

		CollectorItem item = component.getCollectorItems().get(CollectorType.ScopeOwner).get(0);
		
		SprintEstimate estimate = getSprintEstimates(teamId, agileType, estimateMetricType);
		
		List<Feature> list = Collections.singletonList(new Feature());
		list.get(0).setsEstimate(Integer.toString(estimate.getCompleteEstimate()));
		
		Collector collector = collectorRepository.findOne(item.getCollectorId());
		return new DataResponse<>(list, collector.getLastExecuted());
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
			return getEmptyLegacyDataResponse();
		}

		CollectorItem item = component.getCollectorItems().get(CollectorType.ScopeOwner).get(0);

		// Get teamId first from available collector item, based on component
		List<Feature> sprintResponse = getFeaturesForCurrentSprints(teamId, agileType.isPresent()? agileType.get() : null, true);

		Collector collector = collectorRepository.findOne(item.getCollectorId());
		return new DataResponse<>(sprintResponse, collector.getLastExecuted());
	}
	
	private SprintEstimate getSprintEstimates(String teamId, Optional<String> agileType, Optional<String> estimateMetricType) {
		List<Feature> storyEstimates = getFeaturesForCurrentSprints(teamId, agileType.isPresent()? agileType.get() : null, true);

		int totalEstimate = 0;
		int wipEstimate = 0;
		int doneEstimate = 0;
		
		for (Feature tempRs : storyEstimates) {
			String tempStatus = tempRs.getsStatus() != null? tempRs.getsStatus().toLowerCase() : null;

			// if estimateMetricType is hours accumulate time estimate in minutes for better precision ... divide by 60 later
			int estimate = getEstimate(tempRs, estimateMetricType);
			
			totalEstimate += estimate;
			if (tempStatus != null) {
				switch (tempStatus) {
					case "in progress":
					case "waiting":
					case "impeded":
						wipEstimate += estimate;
					break;
					case "done":
					case "accepted":
						doneEstimate += estimate;
					break;
				}
			}
		}
		

		int openEstimate = totalEstimate - wipEstimate - doneEstimate;
		
		if (isEstimateTime(estimateMetricType)) {
			// time estimate is in minutes but we want to return in hours
			totalEstimate /= 60;
			openEstimate /= 60;
			wipEstimate /= 60;
			doneEstimate /= 60;
		}
		
		SprintEstimate response = new SprintEstimate();
		response.setOpenEstimate(openEstimate);
		response.setInProgressEstimate(wipEstimate);
		response.setCompleteEstimate(doneEstimate);
		response.setTotalEstimate(totalEstimate);

		return response;
	}
	
	/**
	 * Get the features that belong to the current sprints
	 * 
	 * @param teamId		the team id
	 * @param agileType		the agile type. Defaults to "scrum" if null
	 * @param minimal		if the resulting list of Features should be minimally populated (see queries for fields)
	 * @return
	 */
	private List<Feature> getFeaturesForCurrentSprints(String teamId, String agileType, boolean minimal) {
		List<Feature> rt = new ArrayList<Feature>();
		
		String now = getCurrentISODateTime();
		
		if ( FeatureCollectorConstants.SPRINT_KANBAN.equalsIgnoreCase(agileType)) {
			/* 
			 * A feature is part of a kanban sprint if any of the following are true:
			 *   - the feature does not have a sprint set
			 *   - the feature has a sprint set that does not have an end date
			 *   - the feature has a sprint set that has an end date >= EOT (9999-12-31T59:59:59.999999)
			 */
			if (minimal) {
				rt.addAll(featureRepository.findByNullSprintsMinimal(teamId));
				rt.addAll(featureRepository.findByUnendingSprintsMinimal(teamId));
			} else {
				rt.addAll(featureRepository.findByNullSprints(teamId));
				rt.addAll(featureRepository.findByUnendingSprints(teamId));
			}
		} else {
			// default to scrum
			/*
			 * A feature is part of a scrum sprint if any of the following are true:
			 *   - the feature has a sprint set that has start <= now <= end and end < EOT (9999-12-31T59:59:59.999999)
			 */
			if (minimal) {
				rt.addAll(featureRepository.findByActiveEndingSprintsMinimal(teamId, now));
			} else {
				rt.addAll(featureRepository.findByActiveEndingSprints(teamId, now));
			}
		}
		
		return rt;
	}

	private DataResponse<List<Feature>> getEmptyLegacyDataResponse() {
		Feature f = new Feature();
		List<Feature> l = new ArrayList<>();
		l.add(f);
		return new DataResponse<>(l, 0);
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