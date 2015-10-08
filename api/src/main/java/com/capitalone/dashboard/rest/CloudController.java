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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.editors.CaseInsensitiveBuildStatusEditor;
import com.capitalone.dashboard.model.BuildStatus;
import com.capitalone.dashboard.model.CloudComputeAggregatedData;
import com.capitalone.dashboard.model.CloudComputeRawData;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.CloudRequest;
import com.capitalone.dashboard.request.CollectorItemRequest;
import com.capitalone.dashboard.service.CloudService;
import com.capitalone.dashboard.service.CloudServiceImpl;
import com.capitalone.dashboard.service.EncryptionService;

/**
 * REST service managing all requests to the feature repository.
 * 
 */
@RestController
public class CloudController {
	private final CloudService cloudService;
	private final EncryptionService encryptionService;
	private static final String JSON = MediaType.APPLICATION_JSON_VALUE;

	@Autowired
	public CloudController(
			EncryptionService encryptionService,
			CloudService cloudService) {
		this.cloudService = cloudService;
		this.encryptionService = encryptionService;
	}
    
	@RequestMapping(value = "/cloud/aggregate", method = POST, consumes = JSON, produces = JSON)
	public DataResponse<CloudComputeAggregatedData> getAggregatedData(
			@Valid @RequestBody CloudRequest request) {
		return cloudService.getAggregatedData(request.getId());
	}

	@RequestMapping(value = "/cloud/details", method = POST, consumes = JSON, produces = JSON)
	public DataResponse<List<CloudComputeRawData>> getInstanceDetails(
			@Valid @RequestBody CloudRequest request) {
		return cloudService.getInstanceDetails(request.getId());
	}

	@RequestMapping(value = "/cloud/config", method = POST, consumes = JSON, produces = JSON)
	public ResponseEntity<CollectorItem> createCloudConfigCollectorItem(
			@Valid @RequestBody CollectorItemRequest request) {

		final String ACCESS_KEY = "accessKey";
		final String SECRET_KEY = "secretKey";
System.out.println("Inside controller");
		CollectorItem item = null;
		String encAccessKey = encryptionService.encrypt((String) request
				.getOptions().get(ACCESS_KEY));
		String encSecretKey = encryptionService.encrypt((String) request
				.getOptions().get(SECRET_KEY));
		if (!"ERROR".equalsIgnoreCase(encAccessKey)
				&& !"ERROR".equalsIgnoreCase(encSecretKey)) {
			request.getOptions().put(ACCESS_KEY, encAccessKey);
			request.getOptions().put(SECRET_KEY, encSecretKey);
			
			item = cloudService.createCloudConfigCollectorItem(request
					.toCollectorItem());
			return ResponseEntity.status(HttpStatus.CREATED).body(item);
		} else {
			System.out.println("Error out #################");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(request.toCollectorItem());
		}
	}

}
