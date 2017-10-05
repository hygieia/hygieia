package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.ApiToken;
import com.capitalone.dashboard.repository.ApiTokenRepository;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApiTokenServiceTest {

    @Mock
    private ApiTokenRepository apiTokenRepository;

    @InjectMocks
    private ApiTokenServiceImpl service;

    @Test
    public void shouldGetAllApiTokens() {
        ApiToken apiToken = new ApiToken("somesys", "dgferdf1drt5dfgdfh4mh+34dfwr3Wdf",
                1496030399999L);
        Collection<ApiToken> apiTokens = Sets.newHashSet(apiToken, apiToken);
        when(apiTokenRepository.findAll()).thenReturn(apiTokens);

        Collection<ApiToken> result = service.getApiTokens();

        assertTrue(result.contains(apiToken));
        assertTrue(result.size() == 1);
    }
}
