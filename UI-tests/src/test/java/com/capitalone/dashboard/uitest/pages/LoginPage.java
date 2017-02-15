package com.capitalone.dashboard.uitest.pages;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.thucydides.core.annotations.At;
import net.thucydides.core.annotations.DefaultUrl;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@DefaultUrl("https://localhost:3000/#/")
@At(urls={"#HOST/#/"})
public class LoginPage extends PageObject {

	@FindBy(name="username")
	WebElement usernameInput;
	
	@FindBy(name="password")
	WebElement passwordInput;
	
	@FindBy(tagName="button")
	WebElement loginButton;
	
	@FindBy(className="help-block")
	WebElement helpBlock;
	
	public LoginPage(WebDriver driver) {
		super(driver);
	}

	public void enterUsername(String username) {
		usernameInput.sendKeys(username);
	}

	public String getUsername() {
		return usernameInput.getAttribute("value");
	}
	
	public void enterPassword(String password) {
		passwordInput.sendKeys(password);
	}
	
	public void clickLogin() {
		loginButton.click();
	}

	public String getLoginErrorText() {
		return helpBlock.getText();
	}
	
}
