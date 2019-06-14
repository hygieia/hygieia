package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.BinaryArtifact;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.GenericCollectorItem;
import com.capitalone.dashboard.repository.BinaryArtifactRepository;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.CollectorItemRepository;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.GenericCollectorItemRepository;
import com.capitalone.dashboard.request.GenericCollectorItemCreateRequest;
import com.capitalone.dashboard.settings.ApiSettings;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class GenericCollectorItemServiceImpl implements GenericCollectorItemService {

    private final GenericCollectorItemRepository genericCollectorItemRepository;
    private final CollectorRepository collectorRepository;
    private final BuildRepository buildRepository;
    private final CollectorItemRepository collectorItemRepository;
    private final ApiSettings apiSettings;
    private final BinaryArtifactRepository binaryArtifactRepository;


    @Autowired
    public GenericCollectorItemServiceImpl(GenericCollectorItemRepository genericCollectorItemRepository, CollectorRepository collectorRepository,BuildRepository buildRepository, CollectorItemRepository collectorItemRepository,
    BinaryArtifactRepository binaryArtifactRepository, ApiSettings apiSettings) {
        this.genericCollectorItemRepository = genericCollectorItemRepository;
        this.collectorRepository = collectorRepository;
        this.buildRepository = buildRepository;
        this.collectorItemRepository = collectorItemRepository;
        this.binaryArtifactRepository = binaryArtifactRepository;
        this.apiSettings = apiSettings;
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

        if("Artifactory".equalsIgnoreCase(request.getToolName())){
            createGenericBinaryArtifactData(request);
        }

        item = genericCollectorItemRepository.save(item);
        return item.getId().toString();
    }


    @Override
    public String createGenericBinaryArtifactData(GenericCollectorItemCreateRequest request){
        ObjectId id = new ObjectId(request.getBuildId());
        Build currentBuild = buildRepository.findOne(id);
        String artifactName = captureArtifactName(apiSettings.getCapturePattern(),request.getRawData());
        String artifactVersion = captureArtifactVersion(apiSettings.getCapturePattern(),request.getRawData());
        List<CollectorItem> artifactCollectorItems = collectorItemRepository.findByArtifactName(artifactName);
        List<ObjectId> artifactCollectorItemIds = !CollectionUtils.isEmpty(artifactCollectorItems)?artifactCollectorItems.stream().map(CollectorItem::getId).collect(Collectors.toList()):null;
        List<BinaryArtifact> genericBinaryArtifacts = new ArrayList<>();
        for (ObjectId item:artifactCollectorItemIds) {
            BinaryArtifact gba = createGenericBinaryArtifact(artifactName,artifactVersion,item,currentBuild);
            gba = binaryArtifactRepository.save(gba);
            genericBinaryArtifacts.add(gba);
        }
        return genericBinaryArtifacts.stream().map(BinaryArtifact::getId).toString();
    }

    private String captureArtifactName(String capturePattern, String rawData) {
        List<String> regex = Arrays.asList(capturePattern);
        return regex
                .stream().map(Pattern::compile)
                .map(p -> p.matcher(rawData))
                .filter(Matcher::find)
                .findFirst()
                .map(match -> match.group(2))
                .orElse("");
    }

    private String captureArtifactVersion(String capturePattern, String rawData) {
        List<String> regex = Arrays.asList(capturePattern);
        return regex
                .stream().map(Pattern::compile)
                .map(p -> p.matcher(rawData))
                .filter(Matcher::find)
                .findFirst()
                .map(match -> match.group(3))
                .orElse("");
    }

    private BinaryArtifact createGenericBinaryArtifact(String artifactName, String artifactVersion,ObjectId artifactCollectorItemId,Build build){
        BinaryArtifact generic = new BinaryArtifact();
        generic.setArtifactVersion(artifactVersion);
        generic.setArtifactName(artifactName);
        generic.setCollectorItemId(artifactCollectorItemId);
        generic.addBuild(build);
        return generic;
    }

}
