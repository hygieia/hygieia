package com.capitalone.dashboard.rest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.model.CloudAggregatedData;
import com.capitalone.dashboard.model.CloudRawData;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.service.CloudService;

/**
 * REST service managing all requests to the feature repository. * 
 * @author NAA505 
 * @author CUO722
 * @author SAV256
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

	@RequestMapping(value = "/cloud", method = GET, produces = JSON)
	public DataResponse<CloudAggregatedData> RESTgetAll(){
		return this.cloudService.getAccount();
	}

	//to get the detailed view of the instances
	@RequestMapping(value = "/cloud/detailed", method = GET, produces = JSON)
	public DataResponse<List<CloudRawData>> RESTgetInstanceDetail(){
		return this.cloudService.getInstanceDetail();
	}	

	@RequestMapping(value = "/cloud/authenticateUser", method = POST, consumes = JSON, produces = JSON)
	public ResponseEntity<Boolean> authenticateUser(@Valid String username) {
		try {
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(cloudService.authenticate(username));
		}catch(org.springframework.dao.DuplicateKeyException de) {
			return ResponseEntity.status(HttpStatus.OK).body(false);
		}
	}

}