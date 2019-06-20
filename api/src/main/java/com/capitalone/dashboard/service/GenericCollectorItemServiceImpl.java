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
    private static final int ARTIFACT_GROUP =1;
    private static final int ARTIFACT_NAME = 2;
    private static final int ARTIFACT_VERSION =3;


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
        String artifactName = captureArtifactAttributes(apiSettings.getCapturePattern(),request.getRawData(),ARTIFACT_NAME);
        String artifactVersion = captureArtifactAttributes(apiSettings.getCapturePattern(),request.getRawData(),ARTIFACT_VERSION);
        String artifactGroupId = captureArtifactAttributes(apiSettings.getCapturePattern(),request.getRawData(),ARTIFACT_GROUP);
        String path = artifactGroupId+"/"+artifactName;
        List<CollectorItem> artifactCollectorItems = collectorItemRepository.findByArtifactNameAndPath(artifactName,path);
        List<ObjectId> artifactCollectorItemIds = !CollectionUtils.isEmpty(artifactCollectorItems)?artifactCollectorItems.stream().map(CollectorItem::getId).collect(Collectors.toList()):null;
        List<BinaryArtifact> genericBinaryArtifacts = new ArrayList<>();
        for (ObjectId item:artifactCollectorItemIds) {
            BinaryArtifact gba = createGenericBinaryArtifact(artifactName,artifactVersion,item,currentBuild);
            gba = binaryArtifactRepository.save(gba);
            genericBinaryArtifacts.add(gba);
        }
        return genericBinaryArtifacts.stream().map(BinaryArtifact::getId).toString();
    }

    private String captureArtifactAttributes(String capturePattern, String rawData,int group) {
        List<String> regex = Arrays.asList(capturePattern);
        return regex
                .stream().map(Pattern::compile)
                .map(p -> p.matcher(rawData))
                .filter(Matcher::find)
                .findFirst()
                .map(match -> match.group(group))
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
