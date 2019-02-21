package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.GenericCollectorItem;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.GenericCollectorItemRepository;
import com.capitalone.dashboard.request.GenericCollectorItemCreateRequest;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        GenericCollectorItem item = genericCollectorItemRepository.findByToolNameAndRawDataAndRelatedCollectorItem(request.getToolName(), request.getRawData(), new ObjectId(request.getRelatedCollectorItemId()));

        if(item == null) {
            item = new GenericCollectorItem();
            item.setCreationTime(System.currentTimeMillis());
            item.setRawData(request.getRawData());
            item.setToolName(request.getToolName());
        }

        item.setCollectorId(collector.getId());
        item.setSource(request.getSource());
        item.setProcessTime(0);
        try {
            item.setRelatedCollectorItem(new ObjectId(request.getRelatedCollectorItemId()));
            item.setBuildId(new ObjectId(request.getBuildId()));
        } catch (IllegalArgumentException ie) {
            throw new HygieiaException("Bad relatedItemId: " + ie.getMessage(), HygieiaException.BAD_DATA);
        }

        item = genericCollectorItemRepository.save(item);
        return item.getId().toString();
    }
}
