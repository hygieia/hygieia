package com.capitalone.dashboard.service;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalone.dashboard.model.CloudComputeAggregatedData;
import com.capitalone.dashboard.model.CloudComputeRawData;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.repository.CloudAggregatedDataRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.ComponentRepository;

@Service
public class CloudServiceImpl implements CloudService {

	private final CloudAggregatedDataRepository cloudAggregatedDataRepository;
	private final ComponentRepository componentRepository;
	private final CollectorItemRepository collectorItemRepository;

	
	@Autowired
	public CloudServiceImpl(
			CloudAggregatedDataRepository cloudAggregatedDataRepository,
			ComponentRepository cloudConfigRepository,
			CollectorItemRepository collectorItemRepository
			) {
		this.cloudAggregatedDataRepository = cloudAggregatedDataRepository;
		this.componentRepository = cloudConfigRepository;
		this.collectorItemRepository = collectorItemRepository;
	}
//
	@Override
	public DataResponse<CloudComputeAggregatedData> getAggregatedData(
			ObjectId id) {
		Component component = componentRepository.findOne(id);
		CollectorItem item = component.getCollectorItems()
				.get(CollectorType.Cloud).get(0);
		ObjectId collectorItemId = item.getId();
		CloudComputeAggregatedData data = cloudAggregatedDataRepository
				.findByCollectorItemId(collectorItemId);
		if (data != null) {
		data.getDetailList().clear();
		return new DataResponse<CloudComputeAggregatedData>(data,
				data.getLastUpdated());
		} else {
			return new DataResponse<CloudComputeAggregatedData>(data,
					System.currentTimeMillis());
		}
	}


	@Override
	public DataResponse<List<CloudComputeRawData>> getInstanceDetails(
			ObjectId id) {
		Component component = componentRepository.findOne(id);
		CollectorItem item = component.getCollectorItems()
				.get(CollectorType.Cloud).get(0);
		ObjectId collectorItemId = item.getId();
		CloudComputeAggregatedData data = cloudAggregatedDataRepository
				.findByCollectorItemId(collectorItemId);
		List<CloudComputeRawData> list = new ArrayList<CloudComputeRawData>();
		if ((data != null) && (data.getDetailList() != null)) {
			list = data.getDetailList();
			return new DataResponse<List<CloudComputeRawData>>(list,
					data.getLastUpdated());
		} else {
			return new DataResponse<List<CloudComputeRawData>>(list,System.currentTimeMillis());
		}

	}
	
	@Override
	public CollectorItem createCloudConfigCollectorItem(CollectorItem item) {
        CollectorItem existing = collectorItemRepository.findByCollectorAndOptions(
                item.getCollectorId(), item.getOptions());
        return existing == null ? collectorItemRepository.save(item) : existing;
	}
}
