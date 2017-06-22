package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.CaApm;

public interface CaApmService {
	
	//DataResponse<Iterable<CaApm>> getAllManageModules(CaApmRequest request);

	Iterable<CaApm> getAlertsByManageModuleName(String manModuleName);
}
