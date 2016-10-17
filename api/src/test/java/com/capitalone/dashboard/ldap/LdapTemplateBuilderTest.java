package com.capitalone.dashboard.ldap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.DefaultDirObjectFactory;
import org.springframework.ldap.core.support.LdapContextSource;

public class LdapTemplateBuilderTest {

	private static final String URL = "www.some.url.for.testing.com";
	private static final String DN = "uid=%s,ou=enterprisetesting,o=organizationfortesting,c=us";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	
	private LdapTemplateBuilder builder;
	private LdapContextSource source;
	
	@Before
	public void setup() throws Exception {
		builder = new LdapTemplateBuilder();
		source = getContextSource();
	}
	
	@Test
	public void testLocalHelper() throws Exception {
		assertNotNull(getContextSource());
		assertEquals(LdapContextSource.class, getContextSource().getClass());
	}
	
	@Test
	public void testConstructor() {
		assertNotNull(source.getDirObjectFactory());
		assertEquals(DefaultDirObjectFactory.class, source.getDirObjectFactory());
	}
	
	@Test
	public void testWithUrl() {
		LdapTemplateBuilder result = builder.withUrl(URL);

		assertNotNull(source.getUrls());
		assertEquals(1, source.getUrls().length);
		verifyBuilder(result);
	}
	
	@Test
	public void testWithDn() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		LdapTemplateBuilder result = builder.withDn(DN, USERNAME);

		Field field = LdapContextSource.class.getSuperclass().getDeclaredField("userDn");
		field.setAccessible(true);
		
		assertEquals(String.format(DN, USERNAME), field.get(source));
		verifyBuilder(result);
	}

	@Test
	public void testWithPassword() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		LdapTemplateBuilder result = builder.withPassword(PASSWORD);

		Field field = LdapContextSource.class.getSuperclass().getDeclaredField("password");
		field.setAccessible(true);
		
		assertEquals(PASSWORD, field.get(source));
		verifyBuilder(result);
	}
	
	@Test
	public void testBuild() throws Exception {
		LdapTemplate result = builder.withUrl(URL).build();
		
		assertNotNull(result);
		assertEquals(LdapTemplate.class, result.getClass());
	}
	
	@Test
	public void testBuildFail() {
		assertNull(builder.build());
	}
	
	private void verifyBuilder(LdapTemplateBuilder result) {
		assertNotNull(result);
		assertEquals(LdapTemplateBuilder.class, result.getClass());
	}
	
	private LdapContextSource getContextSource() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = builder.getClass().getDeclaredField("ldapContextSource");
		field.setAccessible(true);
		return (LdapContextSource) field.get(builder);
	}
	
}
