package com.capitalone.dashboard.model;

import com.google.common.hash.Hashing;
import org.springframework.data.mongodb.core.mapping.Document;

import java.nio.charset.StandardCharsets;

@Document(collection="apitoken")
public class ApiToken extends BaseModel {

    static final String HASH_PREFIX = "sha512:";
    private String apiUser;
    private String apiKey;
    private Long expirationDt;

    public ApiToken(String apiUser, String apiKey, Long expirationDt) {
        this.apiUser = apiUser;
        this.apiKey = hash(apiKey);
        this.expirationDt = expirationDt;
    }

    public String getApiUser() {
        return apiUser;
    }

    public void setApiUser(String apiUser) {
        this.apiUser = apiUser;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = hash(apiKey);
    }

    public Long getExpirationDt() {
        return expirationDt;
    }

    public void setExpirationDt(Long expirationDt) {
        this.expirationDt = expirationDt;
    }

    static String hash(String apiKey) {
        if (!apiKey.startsWith(HASH_PREFIX)) {
            return HASH_PREFIX + Hashing.sha512().hashString(apiKey, StandardCharsets.UTF_8).toString();
        }
        return apiKey;
    }

    public boolean isHashed() {
        return apiKey.startsWith(HASH_PREFIX);
    }

    public boolean checkApiKey(String apiKey) {
        return hash(this.apiKey).equals(hash(apiKey));
    }

    @Override
    public String toString() {
        return "ApiToken [apiUser=" + apiUser + ", apiKey=" + apiKey + "]";
    }
}
