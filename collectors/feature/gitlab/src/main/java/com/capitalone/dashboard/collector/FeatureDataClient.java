package com.capitalone.dashboard.collector;

import java.util.List;

import com.capitalone.dashboard.model.ScopeOwnerCollectorItem;

public interface FeatureDataClient {
	
	void updateTeams(List<ScopeOwnerCollectorItem> teams);
	
	

}
