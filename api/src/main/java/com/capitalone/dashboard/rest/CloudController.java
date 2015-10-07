package com.capitalone.dashboard.rest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import javax.validation.Valid;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.model.CloudComputeAggregatedData;
import com.capitalone.dashboard.model.CloudComputeRawData;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.CollectorItemRequest;
import com.capitalone.dashboard.service.CloudService;

/**
 * REST service managing all requests to the feature repository.
 * 
 */
@RestController
public class CloudController {
	private static final String JSON = MediaType.APPLICATION_JSON_VALUE;
	private final CloudService cloudService;

	@Autowired
	public CloudController(CloudService cloudService) {
		this.cloudService = cloudService;
	}

	@RequestMapping(value = "/cloud/{collectorItemId}", method = GET, produces = JSON)
	public DataResponse<CloudComputeAggregatedData> getAggregatedData(
			@PathVariable ObjectId collectorItemId) {
		return cloudService.getAggregatedData(collectorItemId);
	}

	@RequestMapping(value = "/cloud/details/{collectorItemId}", method = GET, produces = JSON)
	public DataResponse<List<CloudComputeRawData>> deployStatus(
			@PathVariable ObjectId collectorItemId) {
		return cloudService.getInstanceDetails(collectorItemId);
	}

	@RequestMapping(value = "/cloud/config", method = POST, consumes = JSON, produces = JSON)
	public ResponseEntity<CollectorItem> createCollectorItem(
			@Valid @RequestBody CollectorItemRequest request) {
		CollectorItem item = cloudService
				.createCloudConfigCollectorItem(request.toCollectorItem());
		if (item != null) {
			return ResponseEntity.status(HttpStatus.CREATED).body(item);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(request.toCollectorItem());
		}
	}

}
