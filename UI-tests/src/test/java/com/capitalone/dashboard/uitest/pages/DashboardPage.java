package com.capitalone.dashboard.uitest.pages;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.thucydides.core.annotations.At;
import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.annotations.NamedUrl;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@DefaultUrl("https://localhost:3000/#/dashboard/")
@NamedUrl(name = "open.dashboard", url = "/#/dashboard/{1}")
@At(urls={"#HOST/#/dashboard/*"})
public class DashboardPage extends PageObject {
	
	@FindBy(tagName="nav")
	WebElement currentDashboardHeader;
	
	public String getCurrentDashboardTitle()  {
		WebDriverWait wait = new WebDriverWait(getDriver(), 10);
		wait.until(ExpectedConditions.textToBePresentInElement(currentDashboardHeader, "Widget"));
		String result = currentDashboardHeader.getText().toString();
		return result;
	}
	
}
