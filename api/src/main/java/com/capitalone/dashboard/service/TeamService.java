package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.Team;
import org.bson.types.ObjectId;

import java.util.List;

public interface TeamService {
    /**
     * Retrieves all unique teams
     *
     * @return A data response list of type Scope containing all unique scopes
     */
    Iterable<Team> getAllTeams();

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
    DataResponse<Team> getTeam(ObjectId componentId, String teamId);

    List<Team> getTeamsByCollector(ObjectId collectorId);

}
