package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.CodeQualityType;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.GenericCollectorItem;
import com.capitalone.dashboard.model.QCodeQuality;
import com.capitalone.dashboard.repository.CodeQualityRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.GenericCollectorItemRepository;
import com.capitalone.dashboard.request.CodeQualityCreateRequest;
import com.capitalone.dashboard.request.CodeQualityRequest;
import com.capitalone.dashboard.request.CollectorRequest;
import com.capitalone.dashboard.request.GenericCollectorItemCreateRequest;
import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.mysema.query.BooleanBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GenericCollectorItemServiceImpl implements GenericCollectorItemService {

    private final GenericCollectorItemRepository genericCollectorItemRepository;
    private final CollectorRepository collectorRepository;


    @Autowired
    public GenericCollectorItemServiceImpl(GenericCollectorItemRepository genericCollectorItemRepository, CollectorRepository collectorRepository) {
        this.genericCollectorItemRepository = genericCollectorItemRepository;
        this.collectorRepository = collectorRepository;
    }

    @Override
    public String create(GenericCollectorItemCreateRequest request) throws HygieiaException {
        Collector collector = collectorRepository.findByName(request.getToolName());
        if (collector == null) {
            throw new HygieiaException("No collector for tool name " + request.getToolName(), HygieiaException.BAD_DATA);
        }

        GenericCollectorItem newItem = new GenericCollectorItem();
        newItem.setCreationTime(System.currentTimeMillis());
        newItem.setRawData(request.getRawData());
        newItem.setSource(request.getSource());
        try {
            newItem.setRelatedCollectorItem(new ObjectId(request.getHygieiaId()));
        } catch (IllegalArgumentException ie) {
            throw new HygieiaException("Bad relatedItemId: " + ie.getMessage(), HygieiaException.BAD_DATA);
        }
        newItem.setToolName(request.getToolName());

        GenericCollectorItem existing = genericCollectorItemRepository.findByToolNameAndRawDataAndRelatedCollectorItem(newItem.getToolName(), newItem.getRawData(), newItem.getRelatedCollectorItem());
        if (existing == null) {
            existing = genericCollectorItemRepository.save(newItem);
        }
        return existing.getId().toString();
    }
}
