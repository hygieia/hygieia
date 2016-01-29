package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.BinaryArtifact;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.repository.BinaryArtifactRepository;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.request.BinaryArtifactCreateRequest;
import com.capitalone.dashboard.request.BinaryArtifactSearchRequest;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BinaryArtifactServiceImpl implements BinaryArtifactService {

    private final BinaryArtifactRepository artifactRepository;
    private final BuildRepository buildRepository;

    @Autowired
    public BinaryArtifactServiceImpl(BinaryArtifactRepository artifactRepository, BuildRepository buildRepository) {
        this.artifactRepository = artifactRepository;
        this.buildRepository = buildRepository;
    }

    @Override
    public DataResponse<Iterable<BinaryArtifact>> search(BinaryArtifactSearchRequest request) {
        if ((request.getArtifactGroup() != null) && (request.getArtifactName() != null) && (request.getArtifactVersion() != null)) {
            return new DataResponse<>(artifactRepository.findByArtifactGroupIdAndArtifactNameAndArtifactVersion
                    (request.getArtifactGroup(), request.getArtifactName(), request.getArtifactVersion()), 0);
        }

        if ((request.getArtifactGroup() != null) && (request.getArtifactName() != null)) {
            return new DataResponse<>(artifactRepository.findByArtifactNameAndArtifactVersion(
                    request.getArtifactName(), request.getArtifactVersion()), 0);
        }

        if (request.getArtifactName() != null) {
            return new DataResponse<>(artifactRepository.findByArtifactName(
                    request.getArtifactName()), 0);
        }

        if (request.getArtifactGroup() != null) {
            return new DataResponse<>(artifactRepository.findByArtifactGroupId(
                    request.getArtifactGroup()), 0);
        }

        if (request.getBuildId() != null) {
            Build buildInfo = buildRepository.findOne(request.getBuildId());
            if (buildInfo != null) return new DataResponse<>(artifactRepository.findByBuildInfo(buildInfo), 0);
        }
        return new DataResponse<>(null, 0);

    }


    @Override
    public String create(BinaryArtifactCreateRequest request) throws HygieiaException {
        BinaryArtifact ba = new BinaryArtifact();
        ba.setArtifactName(request.getArtifactName());
        ba.setCanonicalName(request.getCanonicalName());
        ba.setArtifactGroupId(request.getArtifactGroup());
        if (!StringUtils.isEmpty(request.getBuildId())) {
            ObjectId objId = new ObjectId(request.getBuildId());
            ba.setBuildInfo(buildRepository.findOne(objId));
        }
        if (ba.getBuildInfo() == null) {
            throw new HygieiaException("Missing Artifact Build Information", HygieiaException.COLLECTOR_ITEM_CREATE_ERROR);
        }

        ba.setArtifactVersion(request.getArtifactVersion());
        ba.setTimestamp(request.getTimestamp());
        BinaryArtifact existing = existing(ba, ba.getBuildInfo());
        if (existing == null) {
            BinaryArtifact savedArt = artifactRepository.save(ba);
            return (savedArt == null) ?  "" : savedArt.getId().toString();
        }
        return existing.getId().toString();
    }


    private BinaryArtifact existing(BinaryArtifact artifact, Build buildInfo) {
        return artifactRepository.findByArtifactGroupIdAndArtifactNameAndArtifactVersionAndBuildInfo
                (artifact.getArtifactGroupId(), artifact.getArtifactName(),
                        artifact.getArtifactVersion(), buildInfo);
    }
}