package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.CloudInstanceHistory;
import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface CloudInstanceHistoryRepository extends
        CrudRepository<CloudInstanceHistory, ObjectId> {

    Collection<CloudInstanceHistory> findByAccountNumber(String accountNumber);

}
