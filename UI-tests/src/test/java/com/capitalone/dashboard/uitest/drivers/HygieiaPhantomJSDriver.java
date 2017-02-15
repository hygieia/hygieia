package com.capitalone.dashboard.uitest.drivers;

import net.thucydides.core.webdriver.DriverSource;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import com.capitalone.dashboard.uitest.drivers.config.DesiredCapabilitiesBuilder;
import com.capitalone.dashboard.uitest.utils.SystemPathPrepper;

public class HygieiaPhantomJSDriver implements DriverSource {

	@Override
	public WebDriver newDriver()  {
		
		SystemPathPrepper systemPathPrepper = new SystemPathPrepper();
		systemPathPrepper.prepareSystemPath();

		DesiredCapabilitiesBuilder desiredCapabilities = new DesiredCapabilitiesBuilder()
		.withProxy()
		.withProxyAuth()
		.withHttps()
		.withSslProtocol();

		WebDriver driver = new PhantomJSDriver(desiredCapabilities.build());
		
		return driver;
	}

	@Override
	public boolean takesScreenshots() {
		return true;
	}
}