package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.Owner;
import com.capitalone.dashboard.request.DashboardRemoteRequest;
import com.capitalone.dashboard.request.DashboardRequest;
import com.capitalone.dashboard.request.DashboardRequestTitle;
import com.google.gson.Gson;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;


public class DashboardRemoteServiceImplTest {
    @Test
    public void remoteCreate() throws Exception {
        Gson g = new Gson();
        DashboardRemoteRequest drr = new DashboardRemoteRequest();
        DashboardRemoteRequest.DashboardMetaData mD = new DashboardRemoteRequest.DashboardMetaData();
        mD.setTitle("RemoteCreateTest");
        mD.setComponentName("testComponent");
        mD.setApplicationName("testApp");
        mD.setOwner(new Owner("topopal", AuthType.STANDARD));
        drr.setMetaData(mD);
        drr.setMetaData(mD);
        DashboardRemoteRequest.CodeRepoEntry cre = new DashboardRemoteRequest.CodeRepoEntry();
        cre.setToolName("GitHub");
        cre.setUrl("http://github.kdc.capitalone.com/yaf107/TestRepo");
        cre.setBranch("master");
        drr.setCodeRepoEntries(Arrays.asList(cre));
        String s = g.toJson(drr);
        System.out.println(s);
    }

}