package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Cmdb;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CmdbService {

    /**
     * Finds paged results of Configuration Items of a given type.
     *
     * @param configItemType configItem Type
     * @param {@link org.springframework.data.domain.Pageable} object to determine which page to return
     * @return String matching the specified type
     */
    Page<Cmdb> configurationItemsByTypeWithFilter(String configItemType, String filter, Pageable pageable);


}
