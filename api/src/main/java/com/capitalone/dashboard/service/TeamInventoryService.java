package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.TeamInventory;

public interface TeamInventoryService {

	DataResponse<TeamInventory> getTeamData(String teamName, String teamId);
}
