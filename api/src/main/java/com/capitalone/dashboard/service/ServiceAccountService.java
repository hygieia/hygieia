package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.ServiceAccount;
import org.bson.types.ObjectId;

import java.util.Collection;

public interface ServiceAccountService {
    String createAccount(String serviceAccount, String fileNames) ;
    String updateAccount(String serviceAccount, String fileNames, ObjectId id);
    Collection<ServiceAccount> getAllServiceAccounts();
    void deleteAccount(ObjectId id);

}
