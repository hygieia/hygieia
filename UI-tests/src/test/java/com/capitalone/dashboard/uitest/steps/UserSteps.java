package com.capitalone.dashboard.uitest.steps;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.hasItem;
import net.thucydides.core.annotations.Step;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.capitalone.dashboard.uitest.pages.DashboardPage;
import com.capitalone.dashboard.uitest.pages.HomePage;
import com.capitalone.dashboard.uitest.pages.LoginPage;

public class UserSteps {
	
	
	private LoginPage loginPage;
	private HomePage homePage;
	private DashboardPage dashboardPage;
	
	@Step
	public void navigate_to_login_page() {
		loginPage.open();
	}

	@Step
	public void enters_username(String username) {
		loginPage.enterUsername(username);
	}

	@Step
	public void enters_password(String password) {
		loginPage.enterPassword(password);
	}

	@Step
	public void clicks_login() {
		loginPage.clickLogin();
	}
	
	@Step
	public void should_be_viewing_login_page() {
		loginPage.shouldBeDisplayed();
	}
	
	@Step
	public void should_be_viewing_home_page() {
		homePage.shouldBeDisplayed();
	}

	@Step
	public void should_see_name_in_welcome_header(String username) {
		String text = homePage.getWelcomeHeaderText();
		assertTrue(StringUtils.containsIgnoreCase(text, username));
	}
	
	@Step
	public void current_dashboard_header_should_read(String dashboardHeader) {
		WebDriverWait wait = new WebDriverWait(dashboardPage.getDriver(), 10);
		wait.until(ExpectedConditions.urlContains("dashboard"));
		String text = dashboardPage.getCurrentDashboardTitle();
		assertTrue(StringUtils.containsIgnoreCase(text, dashboardHeader));
	}

	@Step
	public void should_see_bad_credentials_error() {
		String text = loginPage.getLoginErrorText();
		assertTrue(StringUtils.containsIgnoreCase(text, "Incorrect username and password"));
	}
	
	@Step
	public void clicks_delete_dashboard() {
		homePage.clickDeleteDashboard();
	}

	@Step
	public void should_not_see_given_dashboard(String dashboardName) {
		assertThat(homePage.getAllDashboards(), not(hasItem(dashboardName)));
	}
	
	@Step
	public void clicks_create_new_dashboard_button() {
		homePage.clickCreateDashboardButton();
	}
	
	@Step
	public void clicks_inner_create_new_dashboard_button() {
		homePage.clickInnerCreateDashboardButton();
	}
	
	@Step
	public void sets_dashboard_type_dropdown_to(String teamDropdownOption) {
		homePage.setDashboardTypeDropdownMenuTo(teamDropdownOption);
	}
	
	@Step
	public void sets_template_dropdown_to(String templateDropdownOption) {
		homePage.setTemplateDropdownTo(templateDropdownOption);
	}
	
	@Step
	public void types_in_to_dashboard_title_input(String dashboardTitle) {
		homePage.typeInToDashboardTitleInput(dashboardTitle);
	}

	@Step
	public void types_in_to_application_title_input(String applicationTitle) {
		homePage.typeInToApplicationTitleInput(applicationTitle);
	}

}
