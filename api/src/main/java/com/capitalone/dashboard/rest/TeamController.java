package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.Team;
import com.capitalone.dashboard.service.TeamService;
import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * REST service managing all requests to the feature repository.
 *
 * @author KFK884
 *
 */
@RestController
public class TeamController {
    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    /**
     * REST endpoint for retrieving all features for a given sprint and team
     * (the sprint is derived)
     *
     * @param teamId
     *            A given scope-owner's source-system ID
     * @return A data response list of type Feature containing all features for
     *         the given team and current sprint
     */
    @RequestMapping(value = "/team/{teamId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public DataResponse<Team> team(
            @RequestParam(value = "component", required = true) String cId,
            @PathVariable String teamId) {
        ObjectId componentId = new ObjectId(cId);
        return this.teamService.getTeam(componentId, teamId);
    }

    /**
     *
     * @param collectorId
     *            A given scope-owner's source-system ID
     * @return A data response list of type Feature containing all features for
     *         the given team and current sprint
     */
    @RequestMapping(value = "/teamcollector/{collectorId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public List<Team> teamsByCollector(
            @PathVariable String collectorId) {
        return this.teamService.getTeamsByCollector(new ObjectId(collectorId));
    }

    /**
     * REST endpoint for retrieving all features for a given sprint and team
     * (the sprint is derived)
     *
     * @return A data response list of type Feature containing all features for
     *         the given team and current sprint
     */
    @RequestMapping(value = "/team", method = GET, produces = APPLICATION_JSON_VALUE)
    public List<Team> allTeams() {
        return Lists.newArrayList(this.teamService.getAllTeams());
    }
}
