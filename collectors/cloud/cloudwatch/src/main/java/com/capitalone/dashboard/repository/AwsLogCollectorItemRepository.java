package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.cloudwatch.model.AwsLogCollectorItem;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by stevegal on 20/06/2018.
 */
public interface AwsLogCollectorItemRepository extends BaseCollectorItemRepository<AwsLogCollectorItem>{

    List<AwsLogCollectorItem> findByName(String name);
}
