package com.capitalone.dashboard.repository;

import static org.junit.Assert.*;

import org.junit.Before;
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
	
	private static int testNumber = 0;
	
	private static String username  ;
	
	@Before
	public void updateUsername(){
		username = "usernameTest" + testNumber;
		testNumber++;
	}
	
    @ClassRule
    public static final EmbeddedMongoDBRule RULE = new EmbeddedMongoDBRule();


    @Autowired
    private AuthenticationRepository authenticationRepository;
    
    
    /*
     * This test checks that adding a duplicate username will create an exception
     */
    @Test(expected=DuplicateKeyException.class)
    public void createDuplicateUserTest() {
    	
    	String username = "username";
    	
    	Authentication user1 = new Authentication(username, "pass1");
    	
    	authenticationRepository.save(user1);
    	
    	Authentication user2 = new Authentication(username, "pass2");
    	
    	// This line should throw a DuplicateKeyException
    	authenticationRepository.save(user2);
    }
    /*
     * This test checks that we ge a null when getting a user which does not exist
     */
    @Test
    public void testGetUserDoesNotExist() {
    	assertNull(authenticationRepository.findByUsername(username));
    }

}