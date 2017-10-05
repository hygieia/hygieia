package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.MaturityModel;

import java.util.List;

public interface MaturityModelService {
	
	MaturityModel getMaturityModel(String profile);
	List<String> getProfiles();
}
