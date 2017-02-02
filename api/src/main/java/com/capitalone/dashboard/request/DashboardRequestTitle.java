package com.capitalone.dashboard.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class DashboardRequestTitle {
	
	@NotNull
	@Size(min = 6, max = 50)
	@Pattern(message = "Special character(s) found", regexp = "^[a-zA-Z0-9 ]*$")
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}