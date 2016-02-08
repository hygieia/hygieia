package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.BinaryArtifact;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.repository.BinaryArtifactRepository;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.request.BinaryArtifactCreateRequest;
import com.capitalone.dashboard.request.BinaryArtifactSearchRequest;
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
            return new DataResponse<>(artifactRepository.findByBuildInfoId(
                    request.getBuildId()), 0);
        }
        return new DataResponse<>(null, 0);

    }

    private Build getBuildById(ObjectId buildId){
        return buildRepository.findOne(buildId);
    }


    @Override
    public String create(BinaryArtifactCreateRequest request) {
        BinaryArtifact ba = new BinaryArtifact();
        ba.setArtifactName(request.getArtifactName());
        ba.setCanonicalName(request.getCanonicalName());
        ba.setArtifactGroupId(request.getArtifactGroup());
        ObjectId objId = new ObjectId(request.getBuildId());
        Build build = getBuildById(objId);
        ba.setBuildInfo(build);
        ba.setArtifactVersion(request.getArtifactVersion());
        ba.setTimestamp(request.getTimestamp());
        BinaryArtifact existing = existing(ba, objId);
        if (existing == null) {
            BinaryArtifact savedArt = artifactRepository.save(ba);
            if (savedArt == null) return "";
            return savedArt.getId().toString();
        }
        return existing.getId().toString();
    }


    private BinaryArtifact existing(BinaryArtifact artifact, ObjectId buildId) {
        Iterable<BinaryArtifact> bas = artifactRepository.findByArtifactGroupIdAndArtifactNameAndArtifactVersion
                (artifact.getArtifactGroupId(), artifact.getArtifactName(),
                        artifact.getArtifactVersion());
        for (BinaryArtifact ba : bas) {
            if ((ba.getBuildInfo() != null) && ba.getBuildInfo().getId().equals(buildId)) {
                return ba;
            }
        }
        return null;
    }
}
