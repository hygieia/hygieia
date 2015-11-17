package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.Authentication;
import org.bson.types.ObjectId;

public interface AuthenticationService {
	
	  /**
     * Fetches all registered users, sorted.
     *
     * @return all users
     */
    Iterable<Authentication> all();


    /**
     * Fetches an AuthenticationObject.
     *
     * @param id authentication unique identifier
     * @return Authentication instance
     */
    Authentication get(ObjectId id);

    /**
     * Creates a new Users and saves it to the store.
     * @param username
     * @param password
     * @return
     */

    String create(String username, String password);

    /**
     * Updates an existing quthentication instance.
     * @param username
     * @param password
     * @return
     */
    String update(String username, String password);

    /**
     * Deletes an existing Authentication instance.
     *
     * @param id unique identifier of authentication to delete
     */
    void delete(ObjectId id);
    
    /**
     * Deletes an existing authentication instance
     */
	void delete(String username);

    /**
     *
     * @param username
     * @param password
     * @return
     */
	boolean authenticate(String username, String password);
}
