package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Cmdb;
import com.capitalone.dashboard.repository.CmdbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CmdbServiceImpl implements CmdbService {

    private final CmdbRepository cmdbRepository;

    @Autowired
    public CmdbServiceImpl(CmdbRepository cmdbRepository) {
        this.cmdbRepository = cmdbRepository;

    }

    @Override
    public Page<Cmdb> configurationItemsByTypeWithFilter(String itemType, String filter, Pageable pageable) {
        Page<Cmdb> configItemString = cmdbRepository.findAllByItemTypeAndConfigurationItemContainingIgnoreCase(
                itemType, filter, pageable);

        return configItemString;
    }

}
