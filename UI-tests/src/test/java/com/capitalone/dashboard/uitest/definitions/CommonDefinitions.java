package com.capitalone.dashboard.uitest.definitions;

import net.thucydides.core.annotations.Steps;

import org.jbehave.core.annotations.AfterScenario;
import org.jbehave.core.annotations.Given;

import com.capitalone.dashboard.uitest.steps.UserSteps;
import com.capitalone.dashboard.uitest.utils.TestPropertiesManager;

public class CommonDefinitions {
	
	private String existingUserUsername = TestPropertiesManager.getExistingUserUsername();
	private String existingUserPassword = TestPropertiesManager.getExistingUserPassword();
	
	
	@Steps
	UserSteps user;
	
	@Given("I am logged in")
	public void logIn() {
		user.navigate_to_login_page();
		user.enters_username(existingUserUsername);
		user.enters_password(existingUserPassword);
		user.clicks_login();
	}
	
	@Given("I navigate to the login page")
	public void navigateToLoginPage() {
		user.navigate_to_login_page();
	}
	
	@Given("I am an authorized project stakeholder")
	public void authorizeStakeholder() {
		user.navigate_to_login_page();
		user.enters_username(existingUserUsername);
		user.enters_password(existingUserPassword);
		user.clicks_login();
	}
	
	@Given("I am on the Hygieia home screen")
	public void navigateToHomeScreen() {
		user.should_be_viewing_home_page();
	}
	
	@AfterScenario
	public void cleanupData() throws InterruptedException {
		user.clears_cookies();
		user.navigate_to_login_page();
		user.enters_username(existingUserUsername);
		user.enters_password(existingUserPassword);
		user.clicks_login();
		user.deletes_all_owned_dashboards();
	}
	
}
