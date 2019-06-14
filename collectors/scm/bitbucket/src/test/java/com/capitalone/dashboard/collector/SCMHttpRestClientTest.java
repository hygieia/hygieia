package com.capitalone.dashboard.collector;

import org.apache.http.client.utils.URIBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SCMHttpRestClientTest {

    @Mock
    private GitSettings settings;

    @Mock
    private RestTemplate restTemplate;


    @Mock
    private ResponseEntity<String> responseEntity;

    @InjectMocks
    private SCMHttpRestClient scmHttpRestClient;


    @Captor
    private ArgumentCaptor<HttpMethod> httpMethodArgumentCaptor;

    @Captor
    private ArgumentCaptor<URI> uriArgumentCaptor;


    @Captor
    private ArgumentCaptor<HttpEntity<Object>> httpEntityArgumentCaptor;

    @Captor
    private ArgumentCaptor<Class> classArgumentCaptor;


    @Test
    public void makeRestCallAndtestBasicAuthentication() throws URISyntaxException {
        URI uri = new URIBuilder(
                "https://mycompany.com/rest/api/1.0/xyz/project/rsa").build();

        given(restTemplate.exchange(uriArgumentCaptor.capture(),
                httpMethodArgumentCaptor.capture(),
                httpEntityArgumentCaptor.capture(),
                classArgumentCaptor.capture()))
                .willReturn(responseEntity);

        //when
        ResponseEntity<String> stringResponseEntity = scmHttpRestClient
                .makeRestCall(uri, "user", "password");

        //then
        assertNotNull(stringResponseEntity);
        assertEquals(classArgumentCaptor.getValue(), String.class);
        assertEquals(uriArgumentCaptor.getValue(), uri);
        HttpEntity<?> entity = httpEntityArgumentCaptor.getValue();

        assertEquals("Basic dXNlcjpwYXNzd29yZA==",
                entity.getHeaders().get("Authorization").iterator().next());

    }

    @Test
    public void makeRestCallAndtestBasicAuthenticationForGlobalCredentials() throws URISyntaxException {
        URI uri = new URIBuilder(
                "https://mycompany.com/rest/api/1.0/xyz/project/rsa").build();

        given(restTemplate.exchange(uriArgumentCaptor.capture(),
                httpMethodArgumentCaptor.capture(),
                httpEntityArgumentCaptor.capture(),
                classArgumentCaptor.capture()))
                .willReturn(responseEntity);

        given(settings.getKey()).willReturn("WI9XFb8cW9bxeS/fwdka0LBXFbqtwvEa");
        given(settings.getPassword()).willReturn("GASWrg0aNQAbMu0LxTjAhg==");
        given(settings.getUsername()).willReturn("user");

        //when
        ResponseEntity<String> stringResponseEntity = scmHttpRestClient
                .makeRestCall(uri, null, null);

        //then
        assertNotNull(stringResponseEntity);
        assertEquals(classArgumentCaptor.getValue(), String.class);
        assertEquals(uriArgumentCaptor.getValue(), uri);
        HttpEntity<?> entity = httpEntityArgumentCaptor.getValue();

        assertEquals("Basic dXNlcjpwYXNzd29yZA==",
                entity.getHeaders().get("Authorization").iterator().next());

    }

}
