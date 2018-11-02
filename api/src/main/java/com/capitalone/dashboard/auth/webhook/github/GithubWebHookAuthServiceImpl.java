package com.capitalone.dashboard.auth.webhook.github;

import com.capitalone.dashboard.model.UserRole;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

@Component
public class GithubWebHookAuthServiceImpl implements GithubWebHookAuthService {
    private static final Log LOG = LogFactory.getLog(GithubWebHookAuthServiceImpl.class);

    @Override
    public Authentication getAuthentication(HttpServletRequest request) {
        LOG.info("Pre-Authenticating the api request with user 'githubWebHookDummyUser' ");

        Collection<UserRole> roles = new ArrayList<>();
        roles.add(UserRole.ROLE_API);

        return new PreAuthenticatedAuthenticationToken("githubWebHookDummyUser", null, createAuthorities(roles));
    }

    private Collection<? extends GrantedAuthority> createAuthorities(Collection<UserRole> authorities) {
        Collection<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
        authorities.forEach(authority -> {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority.name()));
        });

        return grantedAuthorities;
    }
}
