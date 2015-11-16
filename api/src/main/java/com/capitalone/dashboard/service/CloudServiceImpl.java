package com.capitalone.dashboard.service;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.Cloud;
import com.capitalone.dashboard.model.CloudComputeData;
import com.capitalone.dashboard.model.CloudComputeInstanceData;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.repository.CloudRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.ComponentRepository;

@Service
public class CloudServiceImpl implements CloudService {

	private final CloudRepository cloudAggregatedDataRepository;
	private final ComponentRepository componentRepository;
	private final CollectorItemRepository collectorItemRepository;

	@Autowired
	public CloudServiceImpl(CloudRepository cloudAggregatedDataRepository,
			ComponentRepository cloudConfigRepository,
			CollectorItemRepository collectorItemRepository) {
		this.cloudAggregatedDataRepository = cloudAggregatedDataRepository;
		this.componentRepository = cloudConfigRepository;
		this.collectorItemRepository = collectorItemRepository;
	}

	//
	@Override
	public DataResponse<CloudComputeData> getAggregatedData(ObjectId id) {
		Component component = componentRepository.findOne(id);
		CollectorItem item = component.getCollectorItems()
				.get(CollectorType.Cloud).get(0);
		ObjectId collectorItemId = item.getId();
		Cloud data = cloudAggregatedDataRepository
				.findByCollectorItemId(collectorItemId);
		if (data != null) {
			CloudComputeData computeData = data.getCompute();
			if (computeData != null) {
				computeData.getDetailList().clear();
				return new DataResponse<>(computeData,
						computeData.getLastUpdated());
			}
			return new DataResponse<>(new CloudComputeData(),
					System.currentTimeMillis());
		} else {
			return new DataResponse<>(new CloudComputeData(),
					System.currentTimeMillis());
		}
	}

	@Override
	public DataResponse<List<CloudComputeInstanceData>> getInstanceDetails(
			ObjectId id) {
		Component component = componentRepository.findOne(id);
		CollectorItem item = component.getCollectorItems()
				.get(CollectorType.Cloud).get(0);
		ObjectId collectorItemId = item.getId();
		Cloud data = cloudAggregatedDataRepository
				.findByCollectorItemId(collectorItemId);
		List<CloudComputeInstanceData> list = new ArrayList<>();
		if (data != null) {
			CloudComputeData computeData = data.getCompute();
			if ((computeData != null) && (computeData.getDetailList() != null)) {
				list = computeData.getDetailList();
				return new DataResponse<>(list,
						computeData.getLastUpdated());
			} else {
				return new DataResponse<>(list,
						System.currentTimeMillis());
			}

		} else {
			return new DataResponse<>(list,
					System.currentTimeMillis());
		}

	}
}
