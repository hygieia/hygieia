package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.ApiToken;
import com.capitalone.dashboard.util.EncryptionException;

import java.util.Collection;

public interface ApiTokenService {
    Collection<ApiToken> getApiTokens();
    String getApiToken(String apiUser, Long expirationDt) throws EncryptionException, HygieiaException;
    org.springframework.security.core.Authentication authenticate(String username, String password);
}
