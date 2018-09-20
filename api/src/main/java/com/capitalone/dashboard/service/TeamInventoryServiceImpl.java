package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.TeamInventory;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.TeamInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamInventoryServiceImpl implements TeamInventoryService {

	private final TeamInventoryRepository teamInventoryRepository;
	private final CollectorRepository collectorRepository;

	@Autowired
	public TeamInventoryServiceImpl(TeamInventoryRepository teamInventoryRepository,
                          CollectorRepository collectorRepository) {
		this.collectorRepository = collectorRepository;
		this.teamInventoryRepository = teamInventoryRepository;
	}


	@Override
	public DataResponse<TeamInventory> getTeamData(String teamName, String teamId) {
		TeamInventory teamInventory = teamInventoryRepository.findByNameAndTeamId(teamName,teamId);
		Collector collector = collectorRepository.findOne(teamInventory.getCollectorId());
		return new DataResponse<>(teamInventory, collector.getLastExecuted());
	}
}
