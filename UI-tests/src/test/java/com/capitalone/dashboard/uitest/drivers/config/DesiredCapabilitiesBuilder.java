package com.capitalone.dashboard.uitest.drivers.config;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.capitalone.dashboard.uitest.utils.TestPropertiesManager;

public class DesiredCapabilitiesBuilder {
	
	private List<String> cliArgs; 
	
	public DesiredCapabilitiesBuilder() {
		cliArgs = new ArrayList<String>();
	}
	
	public DesiredCapabilitiesBuilder with(String arg) {
		cliArgs.add(arg);
		return this;
	}
	
	public DesiredCapabilitiesBuilder withProxy() {
		String proxy = TestPropertiesManager.getProxyUrl();
		cliArgs.add("--proxy=" + proxy);
		return this;
	}
	
	public DesiredCapabilitiesBuilder withProxyAuth(){
		String username = TestPropertiesManager.getProxyUsername();		
		String password = TestPropertiesManager.getProxyPassword();
			
		cliArgs.add("--proxy-auth=" + username + ":" + password);
		return this;
	}
	
	public DesiredCapabilitiesBuilder withHttps(){
		cliArgs.add("--proxy-type=https");
		return this;
	}

	public DesiredCapabilitiesBuilder withSslProtocol(){
		cliArgs.add("--ssl-protocol=any");
		cliArgs.add("--ignore-ssl-errors=true");
		return this;
	}
	
	public DesiredCapabilities build() {
		DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
		capabilities.setCapability(
		    PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgs);
		return capabilities;
	}
}
