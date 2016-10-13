package com.capitalone.dashboard.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.naming.AuthenticationException;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ldap.core.LdapTemplate;

import com.capitalone.dashboard.config.LdapAuthConfigProperties;
import com.capitalone.dashboard.ldap.LdapTemplateBuilder;

@RunWith(MockitoJUnitRunner.class)
public class LdapAuthenticationServiceImplTest {

	private static final String URL = "www.some.url.for.testing.com";
	private static final String DN = "uid=%s,ou=enterprisetesting,o=organizationfortesting,c=us";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	
	@Mock
	private LdapAuthConfigProperties ldapAuthConfigProperties;
	
	@Mock
	private LdapTemplateBuilder ldapTemplateBuilder;
	
	@Mock
	private LdapTemplate ldapTemplate;
	
	@Spy
	@InjectMocks
	private LdapAuthenticationServiceImpl authService;
	
	@Before
	public void setup() {
		when(ldapAuthConfigProperties.getUrl()).thenReturn(URL);
		when(ldapAuthConfigProperties.getDn()).thenReturn(DN);
		
		when(authService.getLdapTemplateBuilder()).thenReturn(ldapTemplateBuilder);
		when(ldapTemplateBuilder.withUrl(any(String.class))).thenReturn(ldapTemplateBuilder);
		when(ldapTemplateBuilder.withDn(any(String.class))).thenReturn(ldapTemplateBuilder);
		when(ldapTemplateBuilder.withPassword(any(String.class))).thenReturn(ldapTemplateBuilder);
		when(ldapTemplateBuilder.build()).thenReturn(ldapTemplate);
		when(ldapTemplate.lookup(any(String.class))).thenReturn(true);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testAll() {
		authService.all();
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testGet() {
		authService.get(null);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testCreate() {
		authService.create(null, null);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testUpdate() {
		authService.update(null, null);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testDeleteObjId() {
		authService.delete((ObjectId) null);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testDeleteStr() {
		authService.delete((String) null);
	}
	
	@Test
	public void testAuthenticateSuccess() {
		assertTrue(authService.authenticate(USERNAME, PASSWORD));
		
		verifyLdapBuilderTemplate();
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testAuthenticateFail() {
		when(ldapTemplate.lookup(any(String.class))).thenThrow(AuthenticationException.class);
		assertFalse(authService.authenticate(USERNAME, PASSWORD));
		
		verifyLdapBuilderTemplate();
	}
	
	@Test
	public void testAuthenticateFail2() {
		when(ldapTemplateBuilder.build()).thenReturn(null);
		assertFalse(authService.authenticate(USERNAME, PASSWORD));
		
		verifyLdapBuilderTemplate();
	}

	private void verifyLdapBuilderTemplate() {
		verify(ldapTemplateBuilder).withUrl(URL);
		verify(ldapTemplateBuilder).withDn(String.format(DN, USERNAME));
		verify(ldapTemplateBuilder).withPassword(PASSWORD);
		verify(ldapTemplateBuilder).build();
	}
}
