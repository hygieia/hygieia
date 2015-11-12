package com.capitalone.dashboard.service;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.CloudComputeData;
import com.capitalone.dashboard.model.CloudComputeInstanceData;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.repository.CloudComputeDataRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.ComponentRepository;

@Service
public class CloudServiceImpl implements CloudService {

	private final CloudComputeDataRepository cloudAggregatedDataRepository;
	private final ComponentRepository componentRepository;
	private final CollectorItemRepository collectorItemRepository;


	@Autowired
	public CloudServiceImpl(
			CloudComputeDataRepository cloudAggregatedDataRepository,
			ComponentRepository cloudConfigRepository,
			CollectorItemRepository collectorItemRepository
			) {
		this.cloudAggregatedDataRepository = cloudAggregatedDataRepository;
		this.componentRepository = cloudConfigRepository;
		this.collectorItemRepository = collectorItemRepository;
	}
//
	@Override
	public DataResponse<CloudComputeData> getAggregatedData(
			ObjectId id) {
		Component component = componentRepository.findOne(id);
		CollectorItem item = component.getCollectorItems()
				.get(CollectorType.Cloud).get(0);
		ObjectId collectorItemId = item.getId();
		CloudComputeData data = cloudAggregatedDataRepository
				.findByCollectorItemId(collectorItemId);
		if (data != null) {
		data.getDetailList().clear();
		return new DataResponse<CloudComputeData>(data,
				data.getLastUpdated());
		} else {
			return new DataResponse<CloudComputeData>(data,
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
		CloudComputeData data = cloudAggregatedDataRepository
				.findByCollectorItemId(collectorItemId);
		List<CloudComputeInstanceData> list = new ArrayList<CloudComputeInstanceData>();
		if ((data != null) && (data.getDetailList() != null)) {
			list = data.getDetailList();
			return new DataResponse<List<CloudComputeInstanceData>>(list,
					data.getLastUpdated());
		} else {
			return new DataResponse<List<CloudComputeInstanceData>>(list,System.currentTimeMillis());
		}

	}
}
