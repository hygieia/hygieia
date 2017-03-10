package com.capitalone.dashboard.gitlab.model;

import java.util.List;

public class GitlabBoard {

	private String id;
	private List<GitlabList> lists;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<GitlabList> getLists() {
		return lists;
	}

	public void setLists(List<GitlabList> lists) {
		this.lists = lists;
	}

}
