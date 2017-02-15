package com.capitalone.dashboard.uitest.utils;

import java.io.File;

import com.capitalone.dashboard.uitest.enums.OperatingSystem;

public class SystemPathPrepper {
	
	public SystemPathPrepper(){
		
	}
	
	public void prepareSystemPath(){
		OperatingSystem os = OperatingSystem.from(System.getProperty("os.name"));

		String binaryPath = "src/main/resources/bin/drivers" + os.getBinaryDriverPath() + "/phantomjs";
		
		File file = new File(binaryPath);
		file.setExecutable(true);
		
		System.setProperty("phantomjs.binary.path", binaryPath);
	}
}