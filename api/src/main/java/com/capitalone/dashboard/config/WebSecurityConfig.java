package com.capitalone.dashboard.config;

import com.capitalone.dashboard.auth.AuthProperties;
import com.capitalone.dashboard.auth.AuthenticationResultHandler;
import com.capitalone.dashboard.auth.apitoken.ApiTokenAuthenticationProvider;
import com.capitalone.dashboard.auth.apitoken.ApiTokenRequestFilter;
import com.capitalone.dashboard.auth.ldap.CustomUserDetailsContextMapper;
import com.capitalone.dashboard.auth.ldap.LdapLoginRequestFilter;
import com.capitalone.dashboard.auth.sso.SsoAuthenticationFilter;
import com.capitalone.dashboard.auth.standard.StandardLoginRequestFilter;
import com.capitalone.dashboard.auth.token.JwtAuthenticationFilter;
import com.capitalone.dashboard.auth.webhook.github.GithubWebHookAuthService;
import com.capitalone.dashboard.auth.webhook.github.GithubWebHookRequestFilter;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.settings.ApiSettings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.Http401AuthenticationEntryPoint;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.ldap.LdapAuthenticationProviderConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.authentication.NullLdapAuthoritiesPopulator;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackages = "com.capitalone.dashboard.settings")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private GithubWebHookAuthService githubWebHookAuthService;

    @Autowired
    private AuthenticationResultHandler authenticationResultHandler;

    @Autowired
    private AuthenticationProvider standardAuthenticationProvider;

    @Autowired
    private ApiTokenAuthenticationProvider apiTokenAuthenticationProvider;

    @Autowired
    private AuthProperties authProperties;

    @Autowired
    private ApiSettings apiSettings;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().cacheControl();
        http.csrf().disable()
                .authorizeRequests().antMatchers("/appinfo").permitAll()
                .antMatchers("/registerUser").permitAll()
                .antMatchers("/findUser").permitAll()
                .antMatchers("/login**").permitAll()
                //TODO: sample call secured with ROLE_API
                //.antMatchers("/ping").hasAuthority("ROLE_API")
                .antMatchers(HttpMethod.GET, "/**").permitAll()

                // Temporary solution to allow jenkins plugin to send data to the api
                //TODO: Secure with API Key
                .antMatchers(HttpMethod.POST, "/build").permitAll()
                .antMatchers(HttpMethod.POST, "/deploy").permitAll()
                .antMatchers(HttpMethod.POST, "/v2/build").permitAll()
                .antMatchers(HttpMethod.POST, "/v3/build").permitAll()
                .antMatchers(HttpMethod.POST, "/v2/deploy").permitAll()
                .antMatchers(HttpMethod.POST, "/performance").permitAll()
                .antMatchers(HttpMethod.POST, "/artifact").permitAll()
                .antMatchers(HttpMethod.POST, "/quality/test").permitAll()
                .antMatchers(HttpMethod.POST, "/quality/static-analysis").permitAll()
                .antMatchers(HttpMethod.POST, "/v2/quality/test").permitAll()
                .antMatchers(HttpMethod.POST, "/v2/quality/static-analysis").permitAll()
                .antMatchers(HttpMethod.POST, "/generic-item").permitAll()
                .antMatchers(HttpMethod.POST, "/metadata").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(standardLoginRequestFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(ssoAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(ldapLoginRequestFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(apiTokenRequestFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(githubWebhookRequestFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling().authenticationEntryPoint(new Http401AuthenticationEntryPoint("Authorization"));
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        List<AuthType> authenticationProviders = authProperties.getAuthenticationProviders();

        if (authenticationProviders.contains(AuthType.STANDARD)) {
            auth.authenticationProvider(standardAuthenticationProvider);
        }

        if (authenticationProviders.contains(AuthType.LDAP)) {
            configureLdap(auth);
            configureActiveDirectory(auth);
        }

        auth.authenticationProvider(apiTokenAuthenticationProvider);
    }

    private void configureActiveDirectory(AuthenticationManagerBuilder auth) {
        ActiveDirectoryLdapAuthenticationProvider adProvider = activeDirectoryLdapAuthenticationProvider();
        if (adProvider != null) auth.authenticationProvider(adProvider);
    }

    private void configureLdap(AuthenticationManagerBuilder auth) throws Exception {
        String ldapServerUrl = authProperties.getLdapServerUrl();
        String ldapUserDnPattern = authProperties.getLdapUserDnPattern();
        if (StringUtils.isNotBlank(ldapServerUrl) && StringUtils.isNotBlank(ldapUserDnPattern)) {
            LdapAuthenticationProviderConfigurer<AuthenticationManagerBuilder> ldapAuthConfigurer = auth.ldapAuthentication();

            ldapAuthConfigurer
                    .userDnPatterns(ldapUserDnPattern)
                    .contextSource().url(ldapServerUrl);

            if (authProperties.isLdapDisableGroupAuthorization()) {
                ldapAuthConfigurer.ldapAuthoritiesPopulator(new NullLdapAuthoritiesPopulator());
            }
        }
    }

    @Bean
    protected StandardLoginRequestFilter standardLoginRequestFilter() throws Exception {
        return new StandardLoginRequestFilter("/login", authenticationManager(), authenticationResultHandler);
    }

    @Bean
    protected GithubWebHookRequestFilter githubWebhookRequestFilter() throws Exception {
        return new GithubWebHookRequestFilter("/webhook/github/v3", authenticationManager(), githubWebHookAuthService, apiSettings, authenticationResultHandler);
    }

    @Bean
    protected SsoAuthenticationFilter ssoAuthenticationFilter() throws Exception {
        return new SsoAuthenticationFilter("/findUser", authenticationManager(), authenticationResultHandler);
    }

    @Bean
    protected LdapLoginRequestFilter ldapLoginRequestFilter() throws Exception {
        return new LdapLoginRequestFilter("/login/ldap", authenticationManager(), authenticationResultHandler);
    }

    @Bean
    protected ApiTokenRequestFilter apiTokenRequestFilter() throws Exception {
        return new ApiTokenRequestFilter("/**", authenticationManager(), authenticationResultHandler);
    }

    @Bean
    protected ActiveDirectoryLdapAuthenticationProvider activeDirectoryLdapAuthenticationProvider() {
        if (StringUtils.isBlank(authProperties.getAdUrl())) return null;

        ActiveDirectoryLdapAuthenticationProvider provider = new ActiveDirectoryLdapAuthenticationProvider(authProperties.getAdDomain(), authProperties.getAdUrl(),
                authProperties.getAdRootDn());
        provider.setConvertSubErrorCodesToExceptions(true);
        provider.setUseAuthenticationRequestCredentials(true);
        provider.setUserDetailsContextMapper(new CustomUserDetailsContextMapper());
        return provider;
    }

}
