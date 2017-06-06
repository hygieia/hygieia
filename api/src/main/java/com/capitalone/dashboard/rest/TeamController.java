package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.Team;
import com.capitalone.dashboard.service.TeamService;
import com.capitalone.dashboard.util.PaginationHeaderUtility;
import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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
    private PaginationHeaderUtility paginationHeaderUtility;

    @Autowired
    public TeamController(TeamService teamService,PaginationHeaderUtility paginationHeaderUtility) {
        this.teamService = teamService;
        this.paginationHeaderUtility = paginationHeaderUtility;
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

    /**
     *
     * @param collectorId, teamName, pageable
     *
     * @return A data response list of teams
     */
    @RequestMapping(value = "/teamcollector/page/{collectorId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Team>> teamsByCollectorPage(
            @PathVariable String collectorId,@RequestParam(value = "search", required = false, defaultValue = "") String descriptionFilter, @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
        Page<Team> pageTeamItems =  teamService.getTeamByCollectorWithFilter(new ObjectId(collectorId),descriptionFilter,pageable);
        return ResponseEntity
                .ok()
                .headers(paginationHeaderUtility.buildPaginationHeaders(pageTeamItems))
                .body(pageTeamItems.getContent());
    }
}
