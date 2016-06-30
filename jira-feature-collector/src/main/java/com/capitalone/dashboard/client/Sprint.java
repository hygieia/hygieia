package com.capitalone.dashboard.client;

import com.atlassian.jira.rest.client.api.IdentifiableEntity;

/**
 * An object representing a com.atlassian.greenhopper.service.sprint.Sprint.
 * 
 * @author <a href="mailto:MarkRx@users.noreply.github.com">MarkRx</a>
 */
public class Sprint implements IdentifiableEntity<Long> {
	private Long id;
	private String state;
	private String name;
	private String startDateStr;
	private String endDateStr;
	private String completeDateStr;
	private int sequence;
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the sequence
	 */
	public int getSequence() {
		return sequence;
	}
	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	/**
	 * @return the startDateStr
	 */
	public String getStartDateStr() {
		return startDateStr;
	}
	/**
	 * @param startDateStr the startDateStr to set
	 */
	public void setStartDateStr(String startDateStr) {
		this.startDateStr = startDateStr;
	}
	/**
	 * @return the endDateStr
	 */
	public String getEndDateStr() {
		return endDateStr;
	}
	/**
	 * @param endDateStr the endDateStr to set
	 */
	public void setEndDateStr(String endDateStr) {
		this.endDateStr = endDateStr;
	}
	/**
	 * @return the completeDateStr
	 */
	public String getCompleteDateStr() {
		return completeDateStr;
	}
	/**
	 * @param completeDateStr the completeDateStr to set
	 */
	public void setCompleteDateStr(String completeDateStr) {
		this.completeDateStr = completeDateStr;
	}
}
