package com.capitalone.dashboard.uitest.drivers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import com.capitalone.dashboard.uitest.drivers.config.DesiredCapabilitiesBuilder;

import net.thucydides.core.webdriver.DriverSource;

public class HygieiaPhantomJSDriver implements DriverSource {

	@Override
	public WebDriver newDriver()  {
		DesiredCapabilitiesBuilder desiredCapabilities = new DesiredCapabilitiesBuilder();
		
		if(Boolean.valueOf(System.getProperty("SSL_UI"))) {
			desiredCapabilities.withHttps().withSslProtocol();
		}

		WebDriver driver = new PhantomJSDriver(desiredCapabilities.build());
		
		return driver;
	}

	@Override
	public boolean takesScreenshots() {
		return true;
	}
}