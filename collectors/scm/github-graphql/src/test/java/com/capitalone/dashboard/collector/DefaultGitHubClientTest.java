package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.GitHubRepo;
import com.capitalone.dashboard.util.Supplier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultGitHubClientTest {

    @Mock
    private Supplier<RestOperations> restOperationsSupplier;
    @Mock private RestOperations rest;
    private GitHubSettings settings;
    private DefaultGitHubClient defaultGitHubClient;
    private static final String URL_USER = "http://mygithub.com/api/v3/users/";


    @Before
    public void init() {
        when(restOperationsSupplier.get()).thenReturn(rest);
        settings = new GitHubSettings();
        defaultGitHubClient = new DefaultGitHubClient(settings, restOperationsSupplier);
        defaultGitHubClient.setLdapMap(new HashMap<>());

    }

    @Test
    public void getLDAPDN_With_Underscore() {
        String userhyphen = "this-has-underscore";
        String userUnderscore = "this_has_underscore";

        when(rest.exchange(eq(URL_USER + userhyphen), eq(HttpMethod.GET),
                eq(null), eq(String.class)))
                .thenReturn(new ResponseEntity<>(goodLdapResponse(), HttpStatus.OK));
        String ldapUser = defaultGitHubClient.getLDAPDN(getGitRepo(),userUnderscore);
        assertEquals(ldapUser, "CN=ldapUser,OU=Developers,OU=All Users,DC=cof,DC=ds,DC=mycompany,DC=com");
        assertEquals(defaultGitHubClient.getLdapMap().containsKey(userhyphen), true);
        assertEquals(defaultGitHubClient.getLdapMap().containsKey(userUnderscore), false);
        assertEquals(defaultGitHubClient.getLdapMap().get(userhyphen), "CN=ldapUser,OU=Developers,OU=All Users,DC=cof,DC=ds,DC=mycompany,DC=com");
        assertEquals(defaultGitHubClient.getLdapMap().get(userUnderscore), null);
    }


    @Test
    public void getLDAPDN_With_Hyphen() {
        String userhyphen = "this-has-hyphen";

        when(rest.exchange(eq(URL_USER + userhyphen), eq(HttpMethod.GET),
                eq(null), eq(String.class)))
                .thenReturn(new ResponseEntity<>(goodLdapResponse(), HttpStatus.OK));
        String ldapUser = defaultGitHubClient.getLDAPDN(getGitRepo(),userhyphen);
        assertEquals(ldapUser, "CN=ldapUser,OU=Developers,OU=All Users,DC=cof,DC=ds,DC=mycompany,DC=com");
        assertEquals(defaultGitHubClient.getLdapMap().containsKey(userhyphen), true);
    }



    @Test
    public void getLDAPDNSimple() {
        String user = "someuser";

        when(rest.exchange(eq(URL_USER + user), eq(HttpMethod.GET),
                eq(null), eq(String.class)))
                .thenReturn(new ResponseEntity<>(goodLdapResponse(), HttpStatus.OK));
        String ldapUser = defaultGitHubClient.getLDAPDN(getGitRepo(),user);
        assertEquals(ldapUser, "CN=ldapUser,OU=Developers,OU=All Users,DC=cof,DC=ds,DC=mycompany,DC=com");
        assertEquals(defaultGitHubClient.getLdapMap().containsKey(user), true);
    }

    @Test
    public void getLDAPDN_NotFound() {
        String user = "someuser-unknown";

        when(rest.exchange(eq(URL_USER + user), eq(HttpMethod.GET),
                eq(null), eq(String.class)))
                .thenReturn(new ResponseEntity<>("", HttpStatus.OK));
        String ldapUser = defaultGitHubClient.getLDAPDN(getGitRepo(),user);
        assertEquals(ldapUser, "");
        assertEquals(defaultGitHubClient.getLdapMap().containsKey(user), false);
    }

    @Test
    public void getLDAPDN_OtherCharacters() {
        String user = "someuser@#$%&($@#---unknown";

        when(rest.exchange(eq(URL_USER + user), eq(HttpMethod.GET),
                eq(null), eq(String.class)))
                .thenReturn(new ResponseEntity<>(goodLdapResponse(), HttpStatus.OK));
        String ldapUser = defaultGitHubClient.getLDAPDN(getGitRepo(),user);
        assertEquals(ldapUser, "CN=ldapUser,OU=Developers,OU=All Users,DC=cof,DC=ds,DC=mycompany,DC=com");
        assertEquals(defaultGitHubClient.getLdapMap().containsKey(user), true);
    }


    private GitHubRepo getGitRepo() {
        GitHubRepo repo = new GitHubRepo();
        repo.setBranch("master");
        repo.setRepoUrl("http://mygithub.com/user/repo");
        return repo;
    }

    private String goodLdapResponse() {
        return "{ \"ldap_dn\": \"CN=ldapUser,OU=Developers,OU=All Users,DC=cof,DC=ds,DC=mycompany,DC=com\"}";
    }

}