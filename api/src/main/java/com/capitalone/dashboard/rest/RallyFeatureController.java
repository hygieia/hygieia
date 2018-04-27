package com.capitalone.dashboard.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.RallyFeature;
import com.capitalone.dashboard.request.RallyFeatureRequest;
import com.capitalone.dashboard.response.RallyBurnDownResponse;
import com.capitalone.dashboard.service.RallyFeatureService;

@RestController
public class RallyFeatureController {

	private final RallyFeatureService rallyFeatureService;

	@Autowired
	public RallyFeatureController(RallyFeatureService rallyFeatureService) {
		this.rallyFeatureService = rallyFeatureService;
	}

	@RequestMapping(value = "/rally", method = GET, produces = APPLICATION_JSON_VALUE)
	public List<Map<String, Object>> getRallyWidgetData(@Valid RallyFeatureRequest request,@PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
		List<RallyFeature> currentIterations = new ArrayList<>();
		List<Map<String, Object>> consolidatedIterationData = new ArrayList<>();

		 CollectorItem item = rallyFeatureService.getCollectorItem(request);
		 if(item!=null && item.getLastUpdated()!=0) {
			currentIterations = rallyFeatureService.rallyWidgetDataDetails(item);
			if(currentIterations.isEmpty()) {
				return null;
			}
			for (RallyFeature iteration : currentIterations) {
				Map<String, Object> iterationData = new HashMap<>();
				iterationData.put("rallyFeature", iteration);
				iterationData.put("rallyBurnDownData", rallyFeatureService.rallyBurnDownData(iteration));
				consolidatedIterationData.add(iterationData);
			}
		 } else {
				Map<String, Object> iterationData = new HashMap<>();
				iterationData.put("rallyFeature", new RallyFeature());
				iterationData.put("rallyBurnDownData", new RallyBurnDownResponse());
				consolidatedIterationData.add(iterationData);
		 }
		return consolidatedIterationData;
	}

}