package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.util.SuperFeatureComparator;
import com.mysema.query.BooleanBuilder;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.TimeZone;

import javax.xml.bind.DatatypeConverter;

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
	 * 
	 * @return A data response list of type Feature containing a single story
	 */
	@Override
	@SuppressWarnings("PMD.AvoidCatchingNPE") // TODO:...
	public DataResponse<List<Feature>> getStory(ObjectId componentId, String storyNumber) {
		Component component = componentRepository.findOne(componentId);
		DataResponse<List<Feature>> rs;
		try {
			CollectorItem item = component.getCollectorItems().get(CollectorType.ScopeOwner).get(0);
			QScopeOwner team = new QScopeOwner("team");
			BooleanBuilder builder = new BooleanBuilder();

			builder.and(team.collectorItemId.eq(item.getId()));

			// Get one story based on story number, based on component
			List<Feature> story = featureRepository.getStoryByNumber(storyNumber);

			Collector collector = collectorRepository.findOne(item.getCollectorId());
			rs = new DataResponse<>(story, collector.getLastExecuted());
		} catch (NullPointerException e) {
			long x = 0;
			Feature f = new Feature();
			List<Feature> l = new ArrayList<Feature>();
			l.add(f);
			rs = new DataResponse<>(l, x);
		}

		return rs;
	}

	/**
	 * Retrieves all stories for a given team and their current sprint
	 * 
	 * @param componentId
	 *            The ID of the related UI component that will reference
	 *            collector item content from this collector
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * 
	 * @return A data response list of type Feature containing all features for
	 *         the given team and current sprint
	 */
	@SuppressWarnings("PMD.AvoidCatchingNPE") // TODO: Avoid catching NullPointerException; consider removing the cause of the NPE
	@Override
	public DataResponse<List<Feature>> getRelevantStories(ObjectId componentId, String teamId) {
		Component component = componentRepository.findOne(componentId);
		DataResponse<List<Feature>> rs;
		try {
			CollectorItem item = component.getCollectorItems().get(CollectorType.ScopeOwner).get(0);
			QScopeOwner team = new QScopeOwner("team");
			BooleanBuilder builder = new BooleanBuilder();

			builder.and(team.collectorItemId.eq(item.getId()));

			// Get teamId first from available collector item, based on
			// component
			List<Feature> relevantStories = featureRepository.queryByOrderBySStatusDesc(teamId,
					getCurrentISODateTime());

			Collector collector = collectorRepository.findOne(item.getCollectorId());

			rs = new DataResponse<>(relevantStories, collector.getLastExecuted());
		} catch (NullPointerException e) {
			long x = 0;
			Feature f = new Feature();
			List<Feature> l = new ArrayList<Feature>();
			l.add(f);
			rs = new DataResponse<>(l, x);
		}
		return rs;
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
	 * 
	 * @return A data response list of type Feature containing the unique
	 *         features plus their sub features' estimates associated to the
	 *         current sprint and team
	 */
	@SuppressWarnings("PMD.AvoidCatchingNPE") // TODO: Avoid catching NullPointerException; consider removing the cause of the NPE
	@Override
	public DataResponse<List<Feature>> getFeatureEstimates(ObjectId componentId, String teamId) {
		Component component = componentRepository.findOne(componentId);
		CollectorItem item = component.getCollectorItems().get(CollectorType.ScopeOwner).get(0);
		QScopeOwner team = new QScopeOwner("team");
		BooleanBuilder builder = new BooleanBuilder();

		builder.and(team.collectorItemId.eq(item.getId()));

		// Get teamId first from available collector item, based on component
		List<Feature> relevantFeatureEstimates = featureRepository
				.getInProgressFeaturesEstimatesByTeamId(teamId, getCurrentISODateTime());
		Collections.sort(relevantFeatureEstimates, new SuperFeatureComparator());

		List<Feature> relevantSuperFeatureEstimates = new ArrayList<Feature>();
		String lastEpicID = "";
		int lineTotalEstimate = 0;
		try {
			for (ListIterator<Feature> iter = relevantFeatureEstimates.listIterator(); iter
					.hasNext();) {
				Feature f = new Feature();
				Feature tempRs = iter.next();

				if (!tempRs.getsEpicID().isEmpty()) {
					try {
						if (tempRs.getsEpicID().equalsIgnoreCase(lastEpicID)) {
							try {
								lineTotalEstimate += Integer.valueOf((tempRs.getsEstimate()));
							} catch (NumberFormatException | ArrayIndexOutOfBoundsException
									| NullPointerException e) {
								lineTotalEstimate += 0;
							}

							try {
								relevantSuperFeatureEstimates.get(
										relevantSuperFeatureEstimates.size() - 1).setsEstimate(
										Integer.toString(lineTotalEstimate));
							} catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
								relevantSuperFeatureEstimates.get(0).setsEstimate(
										Integer.toString(lineTotalEstimate));
							}
						} else {
							lastEpicID = tempRs.getsEpicID();
							lineTotalEstimate = 0;
							try {
								lineTotalEstimate += Integer.valueOf((tempRs.getsEstimate()));
							} catch (NumberFormatException | ArrayIndexOutOfBoundsException
									| NullPointerException e) {
								lineTotalEstimate += 0;
							}

							f.setId(tempRs.getId());
							f.setsEpicID(tempRs.getsEpicID());
							f.setsEpicNumber(tempRs.getsEpicNumber());
							f.setsEpicName(tempRs.getsEpicName());
							f.setsEstimate(Integer.toString(lineTotalEstimate));

							relevantSuperFeatureEstimates.add(f);
						}
					} catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
						// Error case - this is an unexpected scenario
						relevantSuperFeatureEstimates.add(f);
					}
				}
			}
		} catch (NoSuchElementException nsee) {
			Feature f = new Feature();
			relevantSuperFeatureEstimates.add(f);
		}

		Collector collector = collectorRepository.findOne(item.getCollectorId());

		return new DataResponse<>(relevantSuperFeatureEstimates, collector.getLastExecuted());
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
	 * 
	 * @return A data response list of type Feature containing the total
	 *         estimate number for all features
	 */
	@SuppressWarnings("PMD.AvoidCatchingNPE") // TODO: Avoid catching NullPointerException; consider removing the cause of the NPE
	@Override
	public DataResponse<List<Feature>> getTotalEstimate(ObjectId componentId, String teamId) {
		Component component = componentRepository.findOne(componentId);
		CollectorItem item = component.getCollectorItems().get(CollectorType.ScopeOwner).get(0);
		QScopeOwner team = new QScopeOwner("team");
		BooleanBuilder builder = new BooleanBuilder();

		builder.and(team.collectorItemId.eq(item.getId()));

		// Get teamId first from available collector item, based on component
		List<Feature> storyEstimates = featureRepository.getSprintBacklogTotal(teamId,
				getCurrentISODateTime());

		List<Feature> cumulativeEstimate = new ArrayList<Feature>();
		Feature f = new Feature();
		int lineTotalEstimate = 0;
		try {
			for (ListIterator<Feature> iter = storyEstimates.listIterator(); iter.hasNext();) {
				Feature tempRs = iter.next();

				if (!tempRs.getsEstimate().isEmpty()) {
					try {
						lineTotalEstimate += Integer.parseInt(tempRs.getsEstimate());
					} catch (NumberFormatException | ArrayIndexOutOfBoundsException
							| NullPointerException e) {
						lineTotalEstimate += 0;
					}
				}
			}
		} catch (NoSuchElementException nsee) {
			// Do nothing
		}
		f.setsEstimate(Integer.toString(lineTotalEstimate));
		cumulativeEstimate.add(f);

		Collector collector = collectorRepository.findOne(item.getCollectorId());

		return new DataResponse<>(cumulativeEstimate, collector.getLastExecuted());
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
	 * 
	 * @return A data response list of type Feature containing the in-progress
	 *         estimate number for all features
	 */
	@SuppressWarnings("PMD.AvoidCatchingNPE") // TODO: Avoid catching NullPointerException; consider removing the cause of the NPE
	@Override
	public DataResponse<List<Feature>> getInProgressEstimate(ObjectId componentId, String teamId) {
		Component component = componentRepository.findOne(componentId);
		CollectorItem item = component.getCollectorItems().get(CollectorType.ScopeOwner).get(0);
		QScopeOwner team = new QScopeOwner("team");
		BooleanBuilder builder = new BooleanBuilder();

		builder.and(team.collectorItemId.eq(item.getId()));

		// Get teamId first from available collector item, based on component
		List<Feature> storyEstimates = featureRepository.getSprintBacklogInProgress(teamId,
				getCurrentISODateTime());

		List<Feature> cumulativeEstimate = new ArrayList<Feature>();
		Feature f = new Feature();
		int lineTotalEstimate = 0;
		try {
			for (ListIterator<Feature> iter = storyEstimates.listIterator(); iter.hasNext();) {
				Feature tempRs = iter.next();

				if (!tempRs.getsEstimate().isEmpty()) {
					try {
						lineTotalEstimate += Integer.parseInt(tempRs.getsEstimate());
					} catch (NumberFormatException | ArrayIndexOutOfBoundsException
							| NullPointerException e) {
						lineTotalEstimate += 0;
					}
				}
			}
		} catch (NoSuchElementException nsee) {
			// Do nothing
		}
		f.setsEstimate(Integer.toString(lineTotalEstimate));
		cumulativeEstimate.add(f);

		Collector collector = collectorRepository.findOne(item.getCollectorId());

		return new DataResponse<>(cumulativeEstimate, collector.getLastExecuted());
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
	 * 
	 * @return A data response list of type Feature containing the done estimate
	 *         number for all features
	 */
	@SuppressWarnings("PMD.AvoidCatchingNPE") // TODO: Avoid catching NullPointerException; consider removing the cause of the NPE
	@Override
	public DataResponse<List<Feature>> getDoneEstimate(ObjectId componentId, String teamId) {
		Component component = componentRepository.findOne(componentId);
		CollectorItem item = component.getCollectorItems().get(CollectorType.ScopeOwner).get(0);
		QScopeOwner team = new QScopeOwner("team");
		BooleanBuilder builder = new BooleanBuilder();

		builder.and(team.collectorItemId.eq(item.getId()));

		// Get teamId first from available collector item, based on component
		List<Feature> storyEstimates = featureRepository.getSprintBacklogDone(teamId,
				getCurrentISODateTime());

		List<Feature> cumulativeEstimate = new ArrayList<Feature>();
		Feature f = new Feature();
		int lineTotalEstimate = 0;
		try {
			for (ListIterator<Feature> iter = storyEstimates.listIterator(); iter.hasNext();) {
				Feature tempRs = iter.next();

				if (!tempRs.getsEstimate().isEmpty()) {
					try {
						lineTotalEstimate += Integer.parseInt(tempRs.getsEstimate());
					} catch (NumberFormatException | ArrayIndexOutOfBoundsException
							| NullPointerException e) {
						lineTotalEstimate += 0;
					}
				}
			}
		} catch (NoSuchElementException nsee) {
			// Do nothing
		}
		f.setsEstimate(Integer.toString(lineTotalEstimate));
		cumulativeEstimate.add(f);

		Collector collector = collectorRepository.findOne(item.getCollectorId());

		return new DataResponse<>(cumulativeEstimate, collector.getLastExecuted());
	}

	/**
	 * Retrieves the current sprint's detail for a given team.
	 * 
	 * @param componentId
	 *            The ID of the related UI component that will reference
	 *            collector item content from this collector
	 * @param teamId
	 *            A given scope-owner's source-system ID
	 * 
	 * @return A data response list of type Feature containing several relevant
	 *         sprint fields for the current team's sprint
	 */
	@SuppressWarnings("PMD.AvoidCatchingNPE") // TODO: Avoid catching NullPointerException; consider removing the cause of the NPE
	@Override
	public DataResponse<List<Feature>> getCurrentSprintDetail(ObjectId componentId, String teamId) {
		Component component = componentRepository.findOne(componentId);
		CollectorItem item = component.getCollectorItems().get(CollectorType.ScopeOwner).get(0);
		QScopeOwner team = new QScopeOwner("team");
		BooleanBuilder builder = new BooleanBuilder();

		builder.and(team.collectorItemId.eq(item.getId()));

		// Get teamId first from available collector item, based on component
		List<Feature> sprintResponse = featureRepository.getCurrentSprintDetail(teamId,
				getCurrentISODateTime());

		List<Feature> sprintDetail = new ArrayList<Feature>();
		Feature f = new Feature();
		int i = 0;
		try {
			for (ListIterator<Feature> iter = sprintResponse.listIterator(); i < 1; i++) {
				Feature tempRs = iter.next();
				f.setsSprintID(tempRs.getsSprintID());
				f.setsSprintName(tempRs.getsSprintName());
				f.setsSprintBeginDate(tempRs.getsSprintBeginDate());
				f.setsSprintEndDate(tempRs.getsSprintEndDate());
				sprintDetail.add(f);
			}
		} catch (NoSuchElementException | NumberFormatException | ArrayIndexOutOfBoundsException
				| NullPointerException e) {
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
		String currentISODateTime = DatatypeConverter.printDateTime(Calendar.getInstance(TimeZone
				.getTimeZone("UTC")));
		return currentISODateTime;
	}
}
