package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.ApiToken;
import com.capitalone.dashboard.model.UserRole;
import com.capitalone.dashboard.repository.ApiTokenRepository;
import com.capitalone.dashboard.util.Encryption;
import com.capitalone.dashboard.util.EncryptionException;
import com.capitalone.dashboard.util.UnsafeDeleteException;
import com.google.common.collect.Sets;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Component
public class ApiTokenServiceImpl implements ApiTokenService {

    private static final Logger LOGGER = Logger.getLogger(ApiTokenServiceImpl.class);

    private ApiTokenRepository apiTokenRepository;

    @Autowired
    public ApiTokenServiceImpl(ApiTokenRepository apiTokenRepository) {
        this.apiTokenRepository = apiTokenRepository;
    }

	public Collection<ApiToken> getApiTokens() {
		return Sets.newHashSet(apiTokenRepository.findAll());
	}

    @Override
    public String getApiToken(String apiUser, Long expirationDt) throws EncryptionException, HygieiaException {
        ApiToken apiToken = apiTokenRepository.findByApiUserAndExpirationDt(apiUser, expirationDt);
        String apiKey = "";
        if(apiToken == null) {
            apiKey = Encryption.getStringKey();
            apiToken = new ApiToken(apiUser, apiKey, expirationDt);
            apiTokenRepository.save(apiToken);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            throw new HygieiaException("Token already exists for " + apiUser
                    + " expiring " + sdf.format(new Date(apiToken.getExpirationDt())),
                    HygieiaException.DUPLICATE_DATA);
        }
        return apiKey;
    }

    @Override
    public org.springframework.security.core.Authentication authenticate(String username, String password) {
        List<ApiToken> apiTokens = apiTokenRepository.findByApiUser(username);
        for(ApiToken apiToken : apiTokens) {
            if (username.equalsIgnoreCase(apiToken.getApiUser())) {
                if (apiToken != null && apiToken.checkApiKey(password)) {
                    Date sysdate = Calendar.getInstance().getTime();
                    Date expDt = new Date(apiToken.getExpirationDt());
                    if (compareDates(sysdate, expDt) <= 0) {

                        Collection<UserRole> roles = new ArrayList<>();
                        roles.add(UserRole.ROLE_API);

                        return new UsernamePasswordAuthenticationToken(username,
                            password, createAuthorities(roles));
                    }
                }
            }
        }

        throw new BadCredentialsException("Login Failed: Invalid credentials for user " + username);
    }
    @Override
    public void deleteToken(ObjectId id) {
        ApiToken apiToken = apiTokenRepository.findOne(id);

        if(apiToken == null) {
            throw new UnsafeDeleteException("Cannot delete token " + apiToken.getApiUser());
        }else{
            apiTokenRepository .delete(apiToken);
        }
    }
    @Override
    public String updateToken(Long expirationDt, ObjectId id) throws HygieiaException{
        ApiToken apiToken = apiTokenRepository.findOne(id);
        if(apiToken == null) {
            throw new HygieiaException("Cannot find token for " + apiToken.getApiUser(), HygieiaException.BAD_DATA);
        }else{

            apiToken.setExpirationDt(expirationDt);
            apiTokenRepository.save(apiToken);
        }

        return apiToken.getId().toString();
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