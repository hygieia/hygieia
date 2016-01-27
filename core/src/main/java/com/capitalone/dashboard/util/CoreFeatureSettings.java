package com.capitalone.dashboard.util;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "feature")
public class CoreFeatureSettings {
	private List<String> todoStatuses;
	private List<String> doingStatuses;
	private List<String> doneStatuses;

	public List<String> getTodoStatuses() {
		return todoStatuses;
	}

	public void setTodoStatuses(List<String> todoStatuses) {
		this.todoStatuses = todoStatuses;
	}

	public List<String> getDoingStatuses() {
		return doingStatuses;
	}

	public void setDoingStatuses(List<String> doingStatuses) {
		this.doingStatuses = doingStatuses;
	}

	public List<String> getDoneStatuses() {
		return doneStatuses;
	}

	public void setDoneStatuses(List<String> doneStatuses) {
		this.doneStatuses = doneStatuses;
	}
}
