package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.BinaryArtifact;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.repository.BinaryArtifactRepository;
import com.capitalone.dashboard.request.BinaryArtifactCreateRequest;
import com.capitalone.dashboard.request.BinaryArtifactSearchRequest;

import java.util.Map;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BinaryArtifactServiceImpl implements BinaryArtifactService {

    private final BinaryArtifactRepository artifactRepository;

    @Autowired
    public BinaryArtifactServiceImpl(BinaryArtifactRepository artifactRepository) {
        this.artifactRepository = artifactRepository;
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
        
        if (request.getBuildUrl() != null) {
        	return new DataResponse<>(artifactRepository.findByMetadataBuildUrl(
        			request.getBuildUrl()), 0);
        }
        
        return new DataResponse<>(null, 0);

    }

    @Override
    public String create(BinaryArtifactCreateRequest request) {
		BinaryArtifact ba = new BinaryArtifact();
		ba.setArtifactName(request.getArtifactName());
		ba.setCanonicalName(request.getCanonicalName());
		ba.setArtifactGroupId(request.getArtifactGroup());

		Map<String, Object> metadata = request.getMetadata();
		
		if (metadata != null) {
			for (Map.Entry<String, Object> e : metadata.entrySet()) {
				ba.getMetadata().put(e.getKey(), String.valueOf(e.getValue()));
			}
		}

		ba.setArtifactVersion(request.getArtifactVersion());
		ba.setTimestamp(request.getTimestamp());
		BinaryArtifact existing = existing(ba);
		if (existing == null) {
			BinaryArtifact savedArt = artifactRepository.save(ba);
			if (savedArt == null)
				return "";
			return savedArt.getId().toString();
		}
		return existing.getId().toString();
	}


    private BinaryArtifact existing(BinaryArtifact artifact) {
        Iterable<BinaryArtifact> bas = artifactRepository.findByArtifactGroupIdAndArtifactNameAndArtifactVersion
                (artifact.getArtifactGroupId(), artifact.getArtifactName(),
                        artifact.getArtifactVersion());
        for (BinaryArtifact ba : bas) {
        	
        	// could be null due to old documents
        	if (ba.getMetadata() != null &&
        			ObjectUtils.equals(artifact.getBuildUrl(), ba.getBuildUrl())) {
                return ba;
            }
        }
        return null;
    }
}
