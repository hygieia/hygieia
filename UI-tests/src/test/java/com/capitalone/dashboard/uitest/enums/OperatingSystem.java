package com.capitalone.dashboard.uitest.enums;

public enum OperatingSystem {

	OSX("Mac OS X", "/osx"),
	WINDOWS("Windows 7", "/windows"),
	LINUX("Linux", "/linux");
	
	private String osName;
	private String binaryDriverPath;
	
	private OperatingSystem(String osName, String binaryDriverPath) {
		this.osName = osName;
		this.binaryDriverPath = binaryDriverPath;
	}
	
	public String getOsName() {
		return this.osName;
	}
	
	public String getBinaryDriverPath() {
		return this.binaryDriverPath;
	}
	
	public static OperatingSystem from(String name) {
		for(OperatingSystem os : values()) {
			if(os.getOsName().equals(name)) {
				return os;
			}
		}
		
		throw new IllegalArgumentException("Operating system not found. Please update the Operating System definitions enum and add a new path to the phantomjs binary location");
	}
}
