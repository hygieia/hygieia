package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.ServiceAccount;
import com.capitalone.dashboard.repository.ServiceAccountRepository;
import com.google.common.collect.Sets;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ServiceAccountServiceImpl implements ServiceAccountService {

    private static final Logger LOGGER = Logger.getLogger(ServiceAccountServiceImpl.class);


    private ServiceAccountRepository serviceAccountRepository;


    @Autowired
    public ServiceAccountServiceImpl(ServiceAccountRepository serviceAccountRepository) {
        this.serviceAccountRepository = serviceAccountRepository;
    }

    @Override
    public String createAccount(String serviceAccount, String fileNames){
        ServiceAccount sa = new ServiceAccount(serviceAccount,fileNames);
        serviceAccountRepository.save(sa);
        LOGGER.info("Response ---> "+ sa.toString());
        return sa.toString();
    }

    @Override
    public Collection<ServiceAccount> getAllServiceAccounts() {
        return Sets.newHashSet(serviceAccountRepository.findAll());

    }

    @Override
    public String updateAccount(String serviceAccount, String fileNames, ObjectId id){
        ServiceAccount sa = serviceAccountRepository.findOne(id);
        sa.setServiceAccountName(serviceAccount);
        sa.setFileNames(fileNames);
        serviceAccountRepository.save(sa);
        return sa.toString();
    }

    @Override
    public void deleteAccount(ObjectId id ){
        serviceAccountRepository.delete(id);
    }


}
