package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Component;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.Team;
import com.capitalone.dashboard.repository.CollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.TeamRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {

    private final ComponentRepository componentRepository;
    private final TeamRepository teamRepository;
    private final CollectorRepository collectorRepository;

    /**
     * Default autowired constructor for repositories
     *
     * @param componentRepository
     *            Repository containing components used by the UI (populated by
     *            UI)
     * @param collectorRepository
     *            Repository containing all registered collectors
     * @param teamRepository
     *            Repository containing all scopes
     */
    @Autowired
    public TeamServiceImpl(ComponentRepository componentRepository,
                            CollectorRepository collectorRepository,
                           TeamRepository teamRepository) {
        this.componentRepository = componentRepository;
        this.teamRepository = teamRepository;
        this.collectorRepository = collectorRepository;
    }

    /**
     * Retrieves all unique scopes
     *
     * @return A data response list of type Scope containing all unique scopes
     */
    @Override
    public Iterable<Team> getAllTeams() {
        // Get all available teams
        Iterable<Team> teams = teamRepository.findAll();

        for (Team team : teams) {
            Collector collector = collectorRepository
                    .findOne(team.getCollectorId());
            team.setCollector(collector);
        }

        return teams;
    }

    /**
     * Retrieves the scope information for a given scope source system ID
     *
     * @param componentId
     *            The ID of the related UI component that will reference
     *            collector item content from this collector
     * @param teamId
     *            A given scope's source-system ID
     *
     * @return A data response list of type Scope containing all data for a
     *         given scope source-system ID
     */
    @Override
    public DataResponse<Team> getTeam(ObjectId componentId,
                                              String teamId) {
        Component component = componentRepository.findOne(componentId);
        CollectorItem item = component.getCollectorItems()
                .get(CollectorType.AgileTool).get(0);

        // Get one scope by Id
        Team team = teamRepository.findByTeamId(teamId);

        Collector collector = collectorRepository
                .findOne(item.getCollectorId());

        return new DataResponse<>(team, collector.getLastExecuted());
    }

    /**
     * Retrieves the scope information for a given scope source system ID
     *
     * @param collectorId
     *
     * @return teams
     */
    @Override
    public List<Team>  getTeamsByCollector(ObjectId collectorId) {
        List<Team> teams = teamRepository.findByCollectorId(collectorId);

        return teams;
    }

    /**
     * Retrieves the team information for a given collectorId, teamName, pageable
     *
     * @param collectorId, teamName, pageable
     *
     * @return teams
     */
    @Override
    public Page<Team> getTeamByCollectorWithFilter(ObjectId collectorId, String teamName, Pageable pageable) {
        Page<Team> teams = teamRepository.findAllByCollectorIdAndNameContainingIgnoreCase(collectorId,teamName,pageable);
        return teams;
    }

}
