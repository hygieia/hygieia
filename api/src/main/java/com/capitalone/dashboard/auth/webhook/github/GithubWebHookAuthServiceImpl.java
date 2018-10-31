package com.capitalone.dashboard.auth.webhook.github;

import com.capitalone.dashboard.model.ApiToken;
import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.model.UserRole;
import com.capitalone.dashboard.repository.ApiTokenRepository;
import com.capitalone.dashboard.repository.UserInfoRepository;
import com.capitalone.dashboard.service.ApiTokenServiceImpl;
import com.capitalone.dashboard.settings.ApiSettings;
import com.capitalone.dashboard.webhook.github.GitHubWebHookSettings;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Component
public class GithubWebHookAuthServiceImpl implements GithubWebHookAuthService {
    private static final Logger LOGGER = Logger.getLogger(ApiTokenServiceImpl.class);

    private final UserInfoRepository userInfoRepository;

    @Autowired
    public GithubWebHookAuthServiceImpl(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    public Authentication getAuthentication(HttpServletRequest request, GitHubWebHookSettings gitHubWebHookSettings) {
        if (gitHubWebHookSettings != null) {
            String databaseUserAccount = gitHubWebHookSettings.getDatabaseUserAccount();

            if (!StringUtils.isEmpty(databaseUserAccount)) {
                UserInfo user = userInfoRepository.findByUsername(databaseUserAccount);
                if (user != null) {
                    Collection<UserRole> roles = new ArrayList<>();
                    roles.add(UserRole.ROLE_API);
                    return new PreAuthenticatedAuthenticationToken(databaseUserAccount, null, createAuthorities(roles));
                }
            }
        }

        throw new BadCredentialsException("Login Failed: Invalid credentials");
    }

    private Collection<? extends GrantedAuthority> createAuthorities(Collection<UserRole> authorities) {
        Collection<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
        authorities.forEach(authority -> {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority.name()));
        });

        return grantedAuthorities;
    }

    /**
     *
     * @param argA firstDate
     * @param argB secondDate
     * @return 0 = equal, -1 = firstDate is before secondDate, 1 = firstDate is after secondDate
     */
    private static int compareDates(Date argA, Date argB) {

        if (argA == null || argB == null) {
            return -1;
        }

        int retVal = -1;
        try {
            retVal = argA.compareTo(argB);
            if (retVal == 0) { //if dates are equal.
                return 0;
            } else if (retVal < 0) { //if argA is before argument.
                return -1;
            } else if (retVal > 0) { //if argA is after argument.
                return 1;
            }
        } catch (Exception e) {
            LOGGER.warn("Unable to compare dates", e);
        }

        return retVal;
    }

}
