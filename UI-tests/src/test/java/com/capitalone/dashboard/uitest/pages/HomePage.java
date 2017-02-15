package com.capitalone.dashboard.uitest.pages;

import net.serenitybdd.core.pages.PageObject;
import net.thucydides.core.annotations.At;
import net.thucydides.core.annotations.DefaultUrl;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

@DefaultUrl("https://localhost:3000/#/site")
@At(urls={"#HOST/#/site"})
public class HomePage extends PageObject {
	
	@FindBy(className="welcome-header")
	WebElement welcomeHeader;

	@FindBy(className="create-dashboard-button")
	WebElement createDashboardButton;
	
	@FindBy(css=".widget-modal-heading.ng-binding.ng-scope")
	WebElement createDashboardHeader;
	
	@FindBy(name="dashboardType")
	WebElement dashboardTypeDropdown;
	
	@FindBy(name="selectedTemplate")
	WebElement dashboardTemplateDropdown;
	
	@FindBy(name="dashboardTitle")
	WebElement dashboardTitleInput;
	
	@FindBy(name="applicationName")
	WebElement applicationNameInput;
	
	@FindBy(css=".btn.btn-primary.btn-wide")
	WebElement innerCreateNewDashboardButton;
	
	@FindBy(id="filter")
	WebElement filterByDashboardInput;
	
	@FindBy(css=".fa.fa-lg.fa-trash.text-danger")
	WebElement deleteDashboardButton;
	
	public HomePage(WebDriver driver) {
		super(driver);
	}

	public String getWelcomeHeaderText() {
		return welcomeHeader.getText();
	}

	public void clickDeleteDashboard() {
		deleteDashboardButton.click();
	}
	
	public void clickCreateDashboardButton() {
		createDashboardButton.click();
	}
	
	public void clickInnerCreateDashboardButton() {
		innerCreateNewDashboardButton.click();
	}

	public void setDashboardTypeDropdownMenuTo(String teamDropdownOption) {
		Select dropdown = new Select(dashboardTypeDropdown);
		dropdown.selectByVisibleText(teamDropdownOption);
	}

	public void setTemplateDropdownTo(String templateDropdownOption) {
		Select dropdown = new Select(dashboardTemplateDropdown);
		dropdown.selectByVisibleText(templateDropdownOption);
	}

	public void typeInToDashboardTitleInput(String dashboardTitle) {
		dashboardTitleInput.sendKeys(dashboardTitle);
	}

	public void typeInToApplicationTitleInput(String applicationTitle) {
		applicationNameInput.sendKeys(applicationTitle);
	}

	public void deleteAllOwnedDashboards() throws InterruptedException {
		while(dashboardsToDelete()) {
			int numElementsBeforeDelete = getDriver().findElement(By.id("myDashboardsSection")).findElements(By.id("deleteDashboardButton")).size();
			WebDriverWait wait = new WebDriverWait(getDriver(), 10);
			WebElement dashToDelete = getDriver().findElement(By.id("myDashboardsSection")).findElement(By.id("deleteDashboardButton"));
			dashToDelete.click();
			wait.until(ExpectedConditions.numberOfElementsToBeLessThan(By.id("deleteDashboardButton"), numElementsBeforeDelete));
			
		}
		
	}

	private boolean dashboardsToDelete() throws InterruptedException {
		return getDriver().findElement(By.id("myDashboardsSection")).findElements(By.id("deleteDashboardButton")).size() > 0;
		
	}

}
