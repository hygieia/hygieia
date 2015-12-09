package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.CloudRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CloudServiceImpl implements CloudService {

	private final CloudRepository cloudRepository;
	private final ComponentRepository componentRepository;
	private final CollectorItemRepository collectorItemRepository;

	@Autowired
	public CloudServiceImpl(CloudRepository cloudRepository,
			ComponentRepository cloudConfigRepository,
			CollectorItemRepository collectorItemRepository) {
		this.cloudRepository = cloudRepository;
		this.componentRepository = cloudConfigRepository;
		this.collectorItemRepository = collectorItemRepository;
	}

	//
	@Override
	public DataResponse<CloudComputeData> getAggregatedData(ObjectId id) {
		Component component = componentRepository.findOne(id);
		CollectorItem item = component.getCollectorItems().get(CollectorType.Cloud).get(0);
		ObjectId collectorItemId = item.getId();
		Cloud data = cloudRepository
				.findByCollectorItemId(collectorItemId);
		if (data != null) {
			CloudComputeData computeData = data.getCompute();
			if (computeData != null) {
				computeData.getDetailList().clear();
				return new DataResponse<>(computeData, computeData.getLastUpdated());
			}
		}
		return new DataResponse<>(new CloudComputeData(), System.currentTimeMillis());
	}

	@Override
	public DataResponse<List<CloudComputeInstanceData>> getInstanceDetails(ObjectId id) {
		Component component = componentRepository.findOne(id);
		CollectorItem item = component.getCollectorItems().get(CollectorType.Cloud).get(0);
		ObjectId collectorItemId = item.getId();
		Cloud data = cloudRepository
				.findByCollectorItemId(collectorItemId);
		List<CloudComputeInstanceData> list = new ArrayList<>();
		if (data != null) {
			CloudComputeData computeData = data.getCompute();
			if ((computeData != null) && (computeData.getDetailList() != null)) {
				list = computeData.getDetailList();
				return new DataResponse<>(list,
						computeData.getLastUpdated());
			}
		}
		return new DataResponse<>(list,
				System.currentTimeMillis());
	}
}
