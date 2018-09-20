package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.model.TeamInventory;
import com.capitalone.dashboard.request.TeamInventoryRequest;
import com.capitalone.dashboard.service.TeamInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class TeamInventoryController {

    private final TeamInventoryService teamInventoryService;

    @Autowired
    public TeamInventoryController(TeamInventoryService teamInventoryService) {

        this.teamInventoryService = teamInventoryService;
    }

    @RequestMapping(value = "/teamInventory", method = GET,
            produces = APPLICATION_JSON_VALUE)
    public DataResponse<TeamInventory> getTeamWidgetData(@Valid TeamInventoryRequest request) {
        String teamName = request.getTeamName();
        String teamId = request.getTeamId();
        DataResponse<TeamInventory> teamInventory = teamInventoryService.getTeamData(teamName,teamId);
        return teamInventory;

    }

}
