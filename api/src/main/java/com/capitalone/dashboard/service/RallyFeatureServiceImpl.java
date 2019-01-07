package com.capitalone.dashboard.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.RallyBurnDownData;
import com.capitalone.dashboard.model.RallyFeature;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.RallyBurnDownRepository;
import com.capitalone.dashboard.repository.RallyFeatureRepository;
import com.capitalone.dashboard.request.RallyFeatureRequest;
import com.capitalone.dashboard.response.RallyBurnDownResponse;

@Service
public class RallyFeatureServiceImpl implements RallyFeatureService {
	
	private final RallyFeatureRepository rallyFeatureRepository;
	private final RallyBurnDownRepository rallyBurnDownRepository;

	private final CollectorRepository collectorRepository;
	private final ComponentRepository componentRepository;

	@Autowired
	public RallyFeatureServiceImpl(RallyFeatureRepository rallyFeatureRepository,
			RallyBurnDownRepository rallyBurnDownRepository,
			CollectorRepository collectorRepository,
			ComponentRepository componentRepository) {
		this.rallyFeatureRepository = rallyFeatureRepository;
		this.rallyBurnDownRepository = rallyBurnDownRepository;
		this.collectorRepository = collectorRepository;
		this.componentRepository = componentRepository;
	}

	@Override
	public List<RallyFeature> rallyProjectIterationType(String projectId) {
		List<RallyFeature> iteraioncount = rallyFeatureRepository.findByIterationLists(projectId);
		Set<RallyFeature> s1 = new HashSet<RallyFeature>();
		for (int i = 0; i < iteraioncount.size(); i++) {
			s1.add(iteraioncount.get(i));
		}
		iteraioncount.clear();
		iteraioncount.addAll(s1);
		return iteraioncount;
	}

	public List<RallyFeature> rallyWidgetDataDetails(CollectorItem collectorItem) {
		List<RallyFeature> currentIteration = rallyFeatureRepository
				.findByCollectorItemIdAndRemainingDaysNot(collectorItem.getId(),0);
		return currentIteration;
	}

	
	public CollectorItem getCollectorItem(RallyFeatureRequest request) {
		Component component = componentRepository.findOne(request.getComponentId());
		if (component == null) {
			return null;
		}

		List<CollectorItem> collectorItems = component.getCollectorItems(CollectorType.AgileTool);
		ObjectId collectorId = null;
		Optional<Collector> collector = collectorRepository.findByCollectorTypeAndName(CollectorType.AgileTool, "Rally")
															.stream()
															.findFirst();
		collectorId = collector.get().getId();

		for(CollectorItem collectorItem : collectorItems){
			if(collectorItem.getCollectorId().equals(collectorId) && collectorItem.getOptions().get("projectId").equals(request.getProjectId())){
				return collectorItem;
			}
		}
		
		return null;
	}

	@Override
	public RallyBurnDownResponse rallyBurnDownData(RallyFeature request) {

		RallyBurnDownResponse rallyBurnDownResponse = new RallyBurnDownResponse();
		List<String> iterationDates = new ArrayList<>();
		List<String> toDoHours = new ArrayList<>();
		List<Double> acceptedPoints = new ArrayList<>();
		List<Double> taskEstimateArray = new ArrayList<>();

		RallyBurnDownData burnDownData = rallyBurnDownRepository.findByIterationIdAndProjectId(request.getIterationId(),
				request.getProjectId().toString());
		if(burnDownData!=null) {
		for (Map<String, String> burnDownDetail : burnDownData.getBurnDownData()) {
			iterationDates.add(burnDownDetail.get(RallyBurnDownData.ITERATION_DATE).substring(5, 10));
			toDoHours.add(burnDownDetail.get(RallyBurnDownData.ITERATION_TO_DO_HOURS));
			acceptedPoints.add(Double.parseDouble(burnDownDetail.get(RallyBurnDownData.ACCEPTED_POINTS)));
		}

		Double maximumTotalEstimate = burnDownData.getTotalEstimate();
		Double estimateDiff = maximumTotalEstimate / (burnDownData.getBurnDownData().size() - 1);

		for (int j = 0; j < burnDownData.getBurnDownData().size(); j++) {
			if (burnDownData.getBurnDownData().size() - j == 1) {
				taskEstimateArray.add(0d);
			} else {
				taskEstimateArray
						.add(BigDecimal.valueOf(maximumTotalEstimate).setScale(2, RoundingMode.HALF_UP).doubleValue());
				maximumTotalEstimate = maximumTotalEstimate - estimateDiff;
			}
		}

		rallyBurnDownResponse.setAcceptedPoints(acceptedPoints);
		rallyBurnDownResponse.setToDoHours(toDoHours);
		rallyBurnDownResponse.setTotalTaskEstimate(taskEstimateArray);
		rallyBurnDownResponse.setIterationDates(iterationDates);
		}
		return rallyBurnDownResponse;

	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}

}
