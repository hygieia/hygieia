package com.capitalone.dashboard.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.CloudAggregatedData;
import com.capitalone.dashboard.model.CloudRawData;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.repository.AWSRawDataRepository;
import com.capitalone.dashboard.repository.CloudAggregatedDataRepository;

@Service
public class CloudServiceImpl implements CloudService {

	private final CloudAggregatedDataRepository cloudAggregatedDataRepository;
	private final AWSRawDataRepository awsRawDataRepository;

	@Autowired
	public CloudServiceImpl(CloudAggregatedDataRepository cloudAggregatedDataRepository, AWSRawDataRepository awsRawDataRepository) {
		this.cloudAggregatedDataRepository = cloudAggregatedDataRepository;
		this.awsRawDataRepository = awsRawDataRepository;
	}

	@Override
	public DataResponse<CloudAggregatedData> getAccount() {
		DataResponse<CloudAggregatedData> dr;
		dr = new DataResponse<CloudAggregatedData>(cloudAggregatedDataRepository.getCloudAccountDocument());
		return dr;
	}

	@Override
	public DataResponse<List<CloudRawData>> getInstanceDetail() {
		DataResponse<List<CloudRawData>> dr;
		dr = new DataResponse<List<CloudRawData>>(awsRawDataRepository.runInstanceDetailList("cof-sandbox-dev"));
		return dr;
	}
	
	@Override
	public boolean authenticate(String username) {
		boolean flag=false;
		List<CloudAggregatedData> authenticationList= cloudAggregatedDataRepository.verifyAccount(username);
		//if ret list of usernames is not zero, then username exists and ret true
		if(authenticationList.size()!=0)
			flag=true; 
		return flag;
	}

}
