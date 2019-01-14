package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Epic;
import com.capitalone.dashboard.model.FeatureEpicResult;
import com.capitalone.dashboard.model.JiraMode;
import com.capitalone.dashboard.model.Scope;
import com.capitalone.dashboard.model.Team;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface JiraClient {

	FeatureEpicResult getIssues(Team board);

	FeatureEpicResult getIssues(Scope project);

	Set<Scope> getProjects();

	List<Team> getBoards();

	List<Team> getTeams();

	Epic getEpic(String epicKey, Map<String, Epic> epicMap);

	List<String> getAllIssueIds(String id, JiraMode mode);
}
