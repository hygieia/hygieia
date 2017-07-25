package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Cmdb;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CmdbService {

    /**
     * Finds paged results of Configuration Items of a given type.
     *
     * @param configItemType configItem Type
     * @param {@link org.springframework.data.domain.Pageable} object to determine which page to return
     * @return String matching the specified type
     */
    Page<Cmdb> configurationItemsByTypeWithFilter(String configItemType, String filter, Pageable pageable);

    String configurationItemNameByObjectId(ObjectId objectId);
    /**
     *  returns Cmdb object based on object Id
     * @param objectId App or Component object Id
     * @return Cmdb
     */
    Cmdb configurationItemsByObjectId(ObjectId objectId);

    /**
     *  returns Cmdb object based on Configuration Item name
     * @param configItem App or Component
     * @return Cmdb
     */
    Cmdb configurationItemByConfigurationItem(String configItem);

    /**
     * @return List of all BusinessServices
     */
    List<Cmdb> getAllBusServices();

}
