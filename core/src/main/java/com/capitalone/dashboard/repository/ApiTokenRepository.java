package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.ApiToken;
import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ApiTokenRepository extends CrudRepository<ApiToken, ObjectId> {
    ApiToken findByApiUserAndExpirationDt(String apiUser, Long expirationDt);
    ApiToken findByApiUserAndApiKey(String apiUser, String apiKey);
    List<ApiToken> findByApiUser(String apiUser);
}
