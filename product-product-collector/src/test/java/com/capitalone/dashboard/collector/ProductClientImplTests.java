package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.repository.DashboardRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestOperations;

@RunWith(MockitoJUnitRunner.class)
public class ProductClientImplTests {

    @Mock
    private DashboardRepository dashboardRepository;
    @Mock
    private RestOperations rest;
    private ProductSettings settings;
    private ProductClient productClient;
    private ProductClientImpl productClientImpl;

    private static final String URL_TEST = "URL";

    @Before
    public void init() {
        settings = new ProductSettings();
        productClient = productClientImpl = new ProductClientImpl(dashboardRepository);
    }


    @Test
    @Ignore
    public void getTeamDashboards() throws Exception {

    }

}
