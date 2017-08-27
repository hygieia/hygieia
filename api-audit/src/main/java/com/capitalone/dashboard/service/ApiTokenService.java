package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.ApiToken;
import com.capitalone.dashboard.util.EncryptionException;
import org.bson.types.ObjectId;

import java.util.Collection;

public interface ApiTokenService {
    Collection<ApiToken> getApiTokens();
    String getApiToken(String apiUser, Long expirationDt) throws EncryptionException, HygieiaException;
    org.springframework.security.core.Authentication authenticate(String username, String password);
    /**
     * Deletes an existing Token .
     *
     * @param id unique identifier of Token to delete
     */
    void deleteToken(ObjectId id);

    /**
     * Updates expiration date of given token
     * @param expirationDt
     * @param id
     * @return
     * @throws HygieiaException
     */
    String updateToken(Long expirationDt, ObjectId id) throws HygieiaException;
}
