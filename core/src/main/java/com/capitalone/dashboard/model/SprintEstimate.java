package com.capitalone.dashboard.model;

public class SprintEstimate {
	private int openEstimate;
	private int inProgressEstimate;
	private int completeEstimate;
	private int totalEstimate;
	/**
	 * @return the openEstimate
	 */
	public int getOpenEstimate() {
		return openEstimate;
	}
	/**
	 * @param openEstimate the openEstimate to set
	 */
	public void setOpenEstimate(int openEstimate) {
		this.openEstimate = openEstimate;
	}
	/**
	 * @return the inProgressEstimate
	 */
	public int getInProgressEstimate() {
		return inProgressEstimate;
	}
	/**
	 * @param inProgressEstimate the inProgressEstimate to set
	 */
	public void setInProgressEstimate(int inProgressEstimate) {
		this.inProgressEstimate = inProgressEstimate;
	}
	/**
	 * @return the completeEstimate
	 */
	public int getCompleteEstimate() {
		return completeEstimate;
	}
	/**
	 * @param completeEstimate the completeEstimate to set
	 */
	public void setCompleteEstimate(int completeEstimate) {
		this.completeEstimate = completeEstimate;
	}
	/**
	 * @return the totalEstimate
	 */
	public int getTotalEstimate() {
		return totalEstimate;
	}
	/**
	 * @param totalEstimate the totalEstimate to set
	 */
	public void setTotalEstimate(int totalEstimate) {
		this.totalEstimate = totalEstimate;
	}
}
