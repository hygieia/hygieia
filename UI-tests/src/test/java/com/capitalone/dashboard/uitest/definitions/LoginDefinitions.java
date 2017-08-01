package com.capitalone.dashboard.uitest.definitions;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import com.capitalone.dashboard.uitest.steps.UserSteps;
import com.capitalone.dashboard.uitest.utils.TestPropertiesManager;

import net.thucydides.core.annotations.Steps;

public class LoginDefinitions {
	
	private String existingUserUsername = TestPropertiesManager.getExistingUserUsername();
	private String existingUserPassword = TestPropertiesManager.getExistingUserPassword();
	
	@Steps
	UserSteps user;
	
	@When("I enter valid credentials")
	public void enterValidCredentials() {
		user.enters_username(existingUserUsername);
		user.enters_password(existingUserPassword);
	}
	
	@When("I enter invalid credentials")
	public void enterInvalidCredentials() {
		user.enters_username("INVALID USERNAME");
		user.enters_password("INVALID PASSWORD");
	}
	
	@When("I attempt to log in")
	public void clickLogin() {
		user.clicks_login();
	}
	
	@Then("I should be redirected to the home page")
	public void shouldBeRedirectedToHomePage() {
		user.should_be_viewing_home_page();
	}
	
	@Then("I should be on the login page")
	public void shouldBeOnLoginPage() {
		user.should_be_viewing_login_page();
	}
	
	@Then("I should see an error for wrong username or password")
	public void shouldSeeErrorForBadCredentials() {
		user.should_see_bad_credentials_error();
	}
	
	@Then("the welcome header should contain my name")
	public void shouldBeLoggedIn() {
		user.should_see_name_in_welcome_header(existingUserUsername);
	}
	
}
