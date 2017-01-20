package com.capitalone.dashboard.auth.ldap;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.capitalone.dashboard.model.AuthType;

public class LdapAuthenticationDetailsSourceTest {

	@Test
	public void test() {
		LdapAuthenticationDetailsSource detailsSource = new LdapAuthenticationDetailsSource();
		assertEquals(AuthType.LDAP, detailsSource.buildDetails(null));
	}

}
