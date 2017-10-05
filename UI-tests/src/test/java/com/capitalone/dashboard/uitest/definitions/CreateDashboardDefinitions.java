package com.capitalone.dashboard.uitest.definitions;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import com.capitalone.dashboard.uitest.steps.UserSteps;

public class CreateDashboardDefinitions {

	@Steps
	UserSteps user;
	
	@When("I define a new team dashboard named '$dashboardTitle'")
	public void defineNewDashboard(@Named ("dashboardTitle") String dashboardTitle) {
		user.clicks_create_new_dashboard_button();
		user.sets_dashboard_type_dropdown_to("Team dashboard");
		user.sets_template_dropdown_to("Cap One");
		user.types_in_to_dashboard_title_input(dashboardTitle);
		user.types_in_to_application_title_input(dashboardTitle);
	}

	@When("I create the dashboard")
	public void clickCreateButton() {
		user.clicks_inner_create_new_dashboard_button();
	}
	
	@Then("the current dashboard header should read '$dashboardName'")
	public void currentDashboardReads(@Named("dashboardName") String dashboardName) {
		user.current_dashboard_header_should_read(dashboardName);
	}

}
