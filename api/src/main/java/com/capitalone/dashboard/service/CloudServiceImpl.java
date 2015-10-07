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
	private final EncryptionService encryption;
	private static final String ACCESS_KEY = "accessKey";
	private static final String SECRET_KEY = "secretKey";
	private static final String CLOUD_PROVIDER = "AWS";

	@Autowired
	public CloudServiceImpl(
			CloudAggregatedDataRepository cloudAggregatedDataRepository,
			ComponentRepository cloudConfigRepository,
			CollectorItemRepository collectorItemRepository,
			EncryptionService encryption) {
		this.cloudAggregatedDataRepository = cloudAggregatedDataRepository;
		this.componentRepository = cloudConfigRepository;
		this.collectorItemRepository = collectorItemRepository;
		this.encryption = encryption;
	}

	@Override
	public DataResponse<CloudComputeAggregatedData> getAggregatedData(
			ObjectId id) {
		Component component = componentRepository.findOne(id);
		CollectorItem item = component.getCollectorItems()
				.get(CollectorType.Cloud).get(0);
		ObjectId collectorItemId = item.getId();
		CloudComputeAggregatedData data = cloudAggregatedDataRepository
				.getAggregatedData(collectorItemId);
		data.getDetailList().clear();
		return new DataResponse<CloudComputeAggregatedData>(data,
				data.getLastUpdated());
	}

	@Override
	public DataResponse<List<CloudComputeRawData>> getInstanceDetails(
			ObjectId id) {
		Component component = componentRepository.findOne(id);
		CollectorItem item = component.getCollectorItems()
				.get(CollectorType.Cloud).get(0);
		ObjectId collectorItemId = item.getId();
		CloudComputeAggregatedData data = cloudAggregatedDataRepository
				.getAggregatedData(collectorItemId);
		List<CloudComputeRawData> list = new ArrayList<CloudComputeRawData>();
		if ((data != null) && (data.getDetailList() != null)) {
			list = data.getDetailList();
		}
		return new DataResponse<List<CloudComputeRawData>>(list,
				data.getLastUpdated());
	}

	@Override
	public CollectorItem createCloudConfigCollectorItem(CollectorItem item) {
		CollectorItem existing = collectorItemRepository
				.findByCollectorAndOptions(item.getCollectorId(),
						item.getOptions());
		if (existing != null) {
			return existing;
		} else {
			String encAccessKey = encryption.encrypt((String) item.getOptions()
					.get(ACCESS_KEY));
			String encSecretKey = encryption.encrypt((String) item.getOptions()
					.get(SECRET_KEY));
			if (!"ERROR".equalsIgnoreCase(encAccessKey)
					&& !"ERROR".equalsIgnoreCase(encSecretKey)) {
				item.getOptions().put(ACCESS_KEY, encAccessKey);
				item.getOptions().put(SECRET_KEY, encSecretKey);
				return collectorItemRepository.save(item);
			} else {
				return null;
			}
		}
	}
}
