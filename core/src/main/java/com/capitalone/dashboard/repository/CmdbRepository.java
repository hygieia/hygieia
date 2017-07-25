package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Cmdb;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
/**
 * Repository for {@link Cmdb} data.
 */
public interface CmdbRepository extends CrudRepository<Cmdb, ObjectId>  {

    Cmdb findByConfigurationItem(String configurationItem);

    Page<Cmdb> findAllByItemTypeAndConfigurationItemContainingIgnoreCaseAndValidConfigItem(String itemType, String configurationItem, Pageable pageable, boolean valid);

    Cmdb findByConfigurationItemIgnoreCase(String configurationItem);

    List<Cmdb> findAllByItemType(String type);

    Cmdb findByConfigurationItemAndItemType(String confiugrationItem, String itemType);

}
