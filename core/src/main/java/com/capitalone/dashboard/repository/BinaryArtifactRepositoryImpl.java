package com.capitalone.dashboard.repository;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.capitalone.dashboard.model.BinaryArtifact;

public class BinaryArtifactRepositoryImpl implements BinaryArtifactRepositoryCustom {	
	@Autowired
	MongoTemplate template;
	
	@Override
	public Iterable<BinaryArtifact> findByAttributes(Map<String, Object> attributes) {
		Criteria c = null;
		
		boolean first = true;
		for (Map.Entry<String, Object> e : attributes.entrySet()) {
			if (first) {
				c = Criteria.where(e.getKey()).is(e.getValue());
			} else {
				c = c.and(e.getKey()).is(e.getValue());
			}
			
			first = false;
		}
		
		return template.find(new Query(c), BinaryArtifact.class);
	}
}
