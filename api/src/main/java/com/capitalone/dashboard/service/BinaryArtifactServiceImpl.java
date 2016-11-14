package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.BinaryArtifact;
import com.capitalone.dashboard.model.Build;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.JobCollectorItem;
import com.capitalone.dashboard.repository.BinaryArtifactRepository;
import com.capitalone.dashboard.repository.BuildRepository;
import com.capitalone.dashboard.repository.JobRepository;
import com.capitalone.dashboard.request.BinaryArtifactCreateRequest;
import com.capitalone.dashboard.request.BinaryArtifactSearchRequest;

import java.util.Map;
import org.apache.commons.lang.ObjectUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BinaryArtifactServiceImpl implements BinaryArtifactService {

    private final BinaryArtifactRepository artifactRepository;
    private final BuildRepository buildRepository;
    private final JobRepository<? extends JobCollectorItem> jobRepository;

    @Autowired
    public BinaryArtifactServiceImpl(BinaryArtifactRepository artifactRepository, BuildRepository buildRepository, JobRepository<? extends JobCollectorItem> jobRepository) {
        this.artifactRepository = artifactRepository;
        this.buildRepository = buildRepository;
        this.jobRepository = jobRepository;
    }

    @Override
    public DataResponse<Iterable<BinaryArtifact>> search(BinaryArtifactSearchRequest request) {
        if ((request.getArtifactGroup() != null) && (request.getArtifactName() != null) && (request.getArtifactVersion() != null)) {
            return new DataResponse<>(artifactRepository.findByArtifactGroupIdAndArtifactNameAndArtifactVersion
                    (request.getArtifactGroup(), request.getArtifactName(), request.getArtifactVersion()), 0);
        }

        if ((request.getArtifactName() != null) && (request.getArtifactVersion() != null)) {
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

		Map<String, Object> metadata = request.getMetadata();
		
		if (metadata != null) {
			for (Map.Entry<String, Object> e : metadata.entrySet()) {
				ba.getMetadata().put(e.getKey(), String.valueOf(e.getValue()));
			}
		}

		ba.setArtifactVersion(request.getArtifactVersion());
		ba.setTimestamp(request.getTimestamp());
		
		// Set the build information if we have it
		ObjectId buildId = (request.getBuildId() != null && !request.getBuildId().isEmpty())?
				new ObjectId(request.getBuildId()) : null;
		if (buildId != null) {
			setBuildInformation(ba, buildId);
		}
		
		BinaryArtifact existing = existing(ba, buildId);
		if (existing == null) {
			BinaryArtifact savedArt = artifactRepository.save(ba);
			if (savedArt == null)
				return "";
			return savedArt.getId().toString();
		}
		return existing.getId().toString();
	}
    
    private void setBuildInformation(BinaryArtifact ba, ObjectId buildId) {
		Build build = getBuildById(buildId);
		ba.setBuildInfo(build);
		
		// Attempt to deduce metadata information
		if (build != null) { 
			if (ba.getBuildUrl() == null) {
				ba.setBuildUrl(build.getBuildUrl());
			}
			if (ba.getBuildNumber() == null) {
				ba.setBuildNumber(build.getNumber());
			}
			
			JobCollectorItem ci = jobRepository.findOne(build.getCollectorItemId());
			if (ci != null) {
				if (ba.getInstanceUrl() == null) {
					ba.setInstanceUrl(ci.getInstanceUrl());
				}
				if (ba.getJobName() == null) {
					ba.setJobName(ci.getJobName());
				}
				if (ba.getJobUrl() == null) {
					ba.setJobUrl(ci.getJobUrl());
				}
			}
		}
    }


    private BinaryArtifact existing(BinaryArtifact artifact, ObjectId buildId) {
        Iterable<BinaryArtifact> bas = artifactRepository.findByArtifactGroupIdAndArtifactNameAndArtifactVersion
                (artifact.getArtifactGroupId(), artifact.getArtifactName(),
                        artifact.getArtifactVersion());
        for (BinaryArtifact ba : bas) {
        	
        	// could be null due to old documents
        	if (ba.getMetadata() != null 
        			&& artifact.getBuildUrl() != null
        			&& ObjectUtils.equals(artifact.getBuildUrl(), ba.getBuildUrl())) {
                return ba;
            } else if (buildId != null 
            		&& ba.getBuildInfo() != null 
            		&& ba.getBuildInfo().getId().equals(buildId)) {
            	return ba;
            }
        }
        return null;
    }
}
