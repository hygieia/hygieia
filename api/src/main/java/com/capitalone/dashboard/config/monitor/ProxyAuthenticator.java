package com.capitalone.dashboard.config.monitor;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class ProxyAuthenticator extends Authenticator {

	private PasswordAuthentication passwordAuthentication;
	
	public ProxyAuthenticator(PasswordAuthentication passwordAuthentication) {
		this.passwordAuthentication = passwordAuthentication;
	}
	
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return passwordAuthentication;
	}
	
}
