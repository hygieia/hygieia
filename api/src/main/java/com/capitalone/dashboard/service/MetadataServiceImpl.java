package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Metadata;
import com.capitalone.dashboard.repository.MetadataRepository;
import com.capitalone.dashboard.request.MetadataCreateRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetadataServiceImpl implements MetadataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataServiceImpl.class);

    private final MetadataRepository metadataRepository;

    @Autowired
    public MetadataServiceImpl(MetadataRepository metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

    @Override
    public String create(MetadataCreateRequest request) throws HygieiaException {
        final String METHOD_NAME = "MetdataService.create() : ";
        LOGGER.info(METHOD_NAME + " Enter");
        Metadata entity = new Metadata();
        entity.setKey(request.getKey());
        entity.setSource(request.getSource());
        entity.setType(request.getType());
        // check if the raw data is a valid
        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) new JSONParser().parse(String.valueOf(request.getRawData()));
        } catch (ParseException e) {
            LOGGER.error(METHOD_NAME + ExceptionUtils.getStackTrace(e));
            throw new HygieiaException("rawData is malformed JSON", HygieiaException.JSON_FORMAT_ERROR);
        }

        entity.setRawData(jsonObject);
        entity = metadataRepository.save(entity);
        LOGGER.info(METHOD_NAME + " Exit");
        return entity.getId().toString();
    }

}
