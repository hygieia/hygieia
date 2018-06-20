package com.capitalone.dashboard.cloudwatch.repository;

import com.capitalone.dashboard.cloudwatch.model.AwsLogCollectorItem;
import com.capitalone.dashboard.repository.BaseCollectorItemRepository;

import java.util.List;

/**
 * Created by stevegal on 20/06/2018.
 */
public interface AwsLogCollectorItemRepository extends BaseCollectorItemRepository<AwsLogCollectorItem>{

    List<AwsLogCollectorItem> findByName(String name);
}
