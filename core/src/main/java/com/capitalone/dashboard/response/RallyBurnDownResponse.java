package com.capitalone.dashboard.response;

import java.util.List;

public class RallyBurnDownResponse {
	private List<String> iterationDates;
	private List<String> toDoHours;
	private List<Double> acceptedPoints;
	private List<Double> totalTaskEstimate;
	
	public List<Double> getTotalTaskEstimate() {
		return totalTaskEstimate;
	}
	public void setTotalTaskEstimate(List<Double> totalTaskEstimate) {
		this.totalTaskEstimate = totalTaskEstimate;
	}
	public List<String> getIterationDates() {
		return iterationDates;
	}
	public void setIterationDates(List<String> iterationDates) {
		this.iterationDates = iterationDates;
	}
	public List<String> getToDoHours() {
		return toDoHours;
	}
	public void setToDoHours(List<String> toDoHours) {
		this.toDoHours = toDoHours;
	}
	public List<Double> getAcceptedPoints() {
		return acceptedPoints;
	}
	@Override
	public String toString() {
		return "RallyBurnDownResponse [iterationDates=" + iterationDates + ", toDoHours=" + toDoHours
				+ ", acceptedPoints=" + acceptedPoints + "]";
	}
	public void setAcceptedPoints(List<Double> acceptedPoints) {
		this.acceptedPoints = acceptedPoints;
	}
	
	

}
