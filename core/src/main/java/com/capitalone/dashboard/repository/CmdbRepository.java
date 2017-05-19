package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Cmdb;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for {@link Cmdb} data.
 */
public interface CmdbRepository extends CrudRepository<Cmdb, ObjectId>  {

    Cmdb findByConfigurationItem(String configurationItem);

    Page<Cmdb> findAllByItemTypeAndConfigurationItemContainingIgnoreCase(String itemType, String configurationItem, Pageable pageable);

}
