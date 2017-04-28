package com.capitalone.dashboard.gitlab;

import java.util.List;

import com.capitalone.dashboard.gitlab.model.GitlabIssue;
import com.capitalone.dashboard.gitlab.model.GitlabLabel;
import com.capitalone.dashboard.gitlab.model.GitlabProject;
import com.capitalone.dashboard.gitlab.model.GitlabTeam;

public interface GitlabClient {
	
	List<GitlabTeam> getTeams();

	List<GitlabProject> getProjects();

	List<GitlabLabel> getInProgressLabelsForProject(Long id);

	List<GitlabIssue> getIssuesForProject(GitlabProject project);

    List<GitlabProject> getProjectsForTeam(String teamId);

    GitlabProject getProjectById(String projectId);

}
