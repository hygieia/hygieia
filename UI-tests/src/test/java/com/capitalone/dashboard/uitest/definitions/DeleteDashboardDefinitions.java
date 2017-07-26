package com.capitalone.dashboard.uitest.definitions;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import com.capitalone.dashboard.uitest.steps.UserSteps;

public class DeleteDashboardDefinitions {

	@Steps
	UserSteps user;

	@When("I delete a team dashboard")
	public void clickDeleteButton() {
		user.clicks_delete_dashboard();
	}
	
	@Then("the dashboard '$dashboardName' should be deleted")
	public void currentDashboardReads(@Named("dashboardName") String dashboardName) {
		user.should_not_see_given_dashboard(dashboardName);
	}

}
