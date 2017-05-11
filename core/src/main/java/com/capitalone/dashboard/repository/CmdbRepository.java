package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Cmdb;
import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

/**
 * Repository for {@link Cmdb} data.
 */
public interface CmdbRepository extends CrudRepository<Cmdb, ObjectId>{

    Cmdb findByConfigurationItem(String configurationItem);


}
