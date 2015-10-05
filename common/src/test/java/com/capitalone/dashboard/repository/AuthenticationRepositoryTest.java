package com.capitalone.dashboard.repository;

import static org.junit.Assert.fail;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.capitalone.dashboard.config.MongoConfig;
import com.capitalone.dashboard.model.Authentication;


@ContextConfiguration(classes={ MongoConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
public class AuthenticationRepositoryTest {
	

	
    @ClassRule
    public static final EmbeddedMongoDBRule RULE = new EmbeddedMongoDBRule();


    @Autowired
    private AuthenticationRepository authenticationRepository;
    
    /*
     * This test checks that adding a duplicate username will create an exception
     */
    @Test
    public void createUserTest() {
    	
    	String username = "username";
    	
    	Authentication user1 = new Authentication(username, "pass1");
    	
    	authenticationRepository.save(user1);
    	
    	try{
    	Authentication user2 = new Authentication(username, "pass2");
    	
    	// This line should throw a DuplicateKeyException
    	authenticationRepository.save(user2);
    	
    	// If the above line did not throw a DuplicateKeyException, fail the test
    	fail("Didn't throw any Exception");
    	}
    	catch(DuplicateKeyException e){
    		e.printStackTrace();
    	}

    }


}
