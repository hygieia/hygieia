package com.capitalone.dashboard.model;

/**
 * An object representing a com.atlassian.greenhopper.service.sprint.Sprint.
 * 
 * @author <a href="mailto:MarkRx@users.noreply.github.com">MarkRx</a>
 */
public class Sprint {
	private String id;
	private String rapidViewId;
	private String state;
	private String name;
	private String startDateStr;
	private String endDateStr;
	private String completeDateStr;
	private String sequence;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRapidViewId() {
		return rapidViewId;
	}

	public void setRapidViewId(String rapidViewId) {
		this.rapidViewId = rapidViewId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartDateStr() {
		return startDateStr;
	}

	public void setStartDateStr(String startDateStr) {
		this.startDateStr = startDateStr;
	}

	public String getEndDateStr() {
		return endDateStr;
	}

	public void setEndDateStr(String endDateStr) {
		this.endDateStr = endDateStr;
	}

	public String getCompleteDateStr() {
		return completeDateStr;
	}

	public void setCompleteDateStr(String completeDateStr) {
		this.completeDateStr = completeDateStr;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
}
