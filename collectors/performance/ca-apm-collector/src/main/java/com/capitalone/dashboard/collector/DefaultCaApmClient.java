package com.capitalone.dashboard.collector;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;

import org.springframework.stereotype.Component;

import alerts.webservicesapi.server.introscope.wily.com.AlertPollingServiceSoapBindingStub;
import alerts.webservicesapi.server.introscope.wily.com.DAllAlertsSnapshot;
import metricgrouping.webservicesapi.server.introscope.wily.com.ManagementModuleInfo;
import metricgrouping.webservicesapi.server.introscope.wily.com.MetricGroupServiceSoapBindingStub;

@Component
public class DefaultCaApmClient implements CaApmClient {
	
    private String userName;
	private String password;
	   
	@Override
	public DAllAlertsSnapshot[] getAllAlertsSnapshotForManagementModule(CaApmSettings caApmPullSettings,String mngModelName) throws Exception {		
		userName = caApmPullSettings.getUser();
		password = caApmPullSettings.getPassword();

        URL metricWSDL = null;
        metricWSDL = new URL(caApmPullSettings.getAlertWsdl());
        Authenticator.setDefault (new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication (userName, password.toCharArray());
            }
        });
        
        String nameSpaceUri = "urn:com.wily.introscope.server.webservicesapi.alerts";
        String serviceName = "AlertPollingService";
        
        ServiceFactory serviceFactory = ServiceFactory.newInstance();
        Service metricService = (Service)serviceFactory.createService(metricWSDL, new QName(nameSpaceUri, serviceName));        		
        AlertPollingServiceSoapBindingStub alertPollingServiceSoapBindingStub = new AlertPollingServiceSoapBindingStub(metricWSDL, metricService);
		alertPollingServiceSoapBindingStub._setProperty(javax.xml.rpc.Stub.USERNAME_PROPERTY, caApmPullSettings.getUser());
		alertPollingServiceSoapBindingStub._setProperty(javax.xml.rpc.Stub.PASSWORD_PROPERTY, caApmPullSettings.getPassword());		
		DAllAlertsSnapshot[] data = alertPollingServiceSoapBindingStub.getAllAlertsSnapshotForManagementModule(mngModelName);
		return data;
	}
	
	@Override
	public ManagementModuleInfo[] getListOfManagementModules(CaApmSettings caApmPullSettings) throws Exception {		
		
		userName = caApmPullSettings.getUser();
		password = caApmPullSettings.getPassword();

        URL metricWSDL = null;
        metricWSDL = new URL(caApmPullSettings.getModelWsdl());
        Authenticator.setDefault (new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication (userName, password.toCharArray());
            }
        });
        if(metricWSDL == null){
        	System.out.println("Metric WSDL is null");
        }
        System.out.println(metricWSDL.getPort());
        System.out.println(metricWSDL.getHost());
        System.out.println(metricWSDL.getPath());
        
        String nameSpaceUri = "urn:com.wily.introscope.server.webservicesapi.metricgrouping";
        String serviceName = "MetricGroupService";
        ServiceFactory serviceFactory = ServiceFactory.newInstance();
        Service metricService = (Service)serviceFactory.createService(metricWSDL, new QName(nameSpaceUri, serviceName));        		
        MetricGroupServiceSoapBindingStub metricGroupServiceSoapBindingStub = new MetricGroupServiceSoapBindingStub(metricWSDL, metricService);
        metricGroupServiceSoapBindingStub._setProperty(javax.xml.rpc.Stub.USERNAME_PROPERTY, caApmPullSettings.getUser());
        metricGroupServiceSoapBindingStub._setProperty(javax.xml.rpc.Stub.PASSWORD_PROPERTY, caApmPullSettings.getPassword());		
		ManagementModuleInfo[] data = metricGroupServiceSoapBindingStub.getListOfManagementModules();
		return data;
	}
}
