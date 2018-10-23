package com.capitalone.dashboard.auth.ldap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.ldap.LdapAuthenticationProviderConfigurer;
import org.springframework.security.ldap.authentication.NullLdapAuthoritiesPopulator;

import com.capitalone.dashboard.auth.AuthProperties;
import com.capitalone.dashboard.config.WebSecurityConfig;
import com.capitalone.dashboard.model.AuthType;
import com.google.common.collect.Lists;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LdapGroupAuthorization {

    private ObjectPostProcessor<Object> objectPostProcessor = new ObjectPostProcessor<Object>() {
        @Override
        public <O> O postProcess(O object) {
            return null;
        }
    };

    @Spy
    private AuthProperties authProperties = new AuthProperties();

    @Spy
    private AuthenticationManagerBuilder authenticationManagerBuilder = new AuthenticationManagerBuilder(objectPostProcessor);

    @Spy
    private LdapAuthenticationProviderConfigurer<AuthenticationManagerBuilder> ldapAuthenticationProviderConfigurer = new LdapAuthenticationProviderConfigurer<>();

    @InjectMocks
    private WebSecurityConfigTest webSecurityConfig = new WebSecurityConfigTest();

    @Before
    public void init() throws Exception {
        Mockito.when(authenticationManagerBuilder.ldapAuthentication()).thenReturn(ldapAuthenticationProviderConfigurer);

        authProperties.setLdapServerUrl("ldap.company.com");
        authProperties.setLdapBindUser("uid=admin,dc=company,dc=com");
        authProperties.setLdapBindPass("Mys3cr3tp@$$w0rd");
        authProperties.setLdapUserDnPattern("uid={0}");
        authProperties.setAuthenticationProviders(Lists.newArrayList(AuthType.LDAP));
    }

    @Test
    public void testLdapDisableGroupAuthorization() throws Exception {
        authProperties.setLdapDisableGroupAuthorization(true);
        webSecurityConfig.configure(authenticationManagerBuilder);
        verify(ldapAuthenticationProviderConfigurer).ldapAuthoritiesPopulator(Matchers.any(NullLdapAuthoritiesPopulator.class));
    }

    @Test
    public void testLdapGroupAuthorization() throws Exception {
        authProperties.setLdapDisableGroupAuthorization(false);
        webSecurityConfig.configure(authenticationManagerBuilder);
        verify(ldapAuthenticationProviderConfigurer, never()).ldapAuthoritiesPopulator(Matchers.any());
    }

    private class WebSecurityConfigTest extends WebSecurityConfig {
        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            super.configure(auth);
        }
    }
}
