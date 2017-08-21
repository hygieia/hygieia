package com.capitalone.dashboard.gitlab;

import java.util.List;

import com.capitalone.dashboard.gitlab.model.GitlabIssue;
import com.capitalone.dashboard.gitlab.model.GitlabLabel;
import com.capitalone.dashboard.gitlab.model.GitlabProject;
import com.capitalone.dashboard.model.Project;

public interface GitlabClient {

	List<GitlabLabel> getInProgressLabelsForProject(Project project);

	List<GitlabIssue> getIssuesForProject(Project project);

    List<GitlabProject> getProjectsForTeam(String teamName);

}
