/**
 * AlertPollingService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package alerts.webservicesapi.server.introscope.wily.com;

import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceException;

@SuppressWarnings("PMD")
public interface AlertPollingService extends Service {
    public java.lang.String getAlertPollingServiceAddress();

    public IAlertPollingService getAlertPollingService() throws ServiceException;

    public IAlertPollingService getAlertPollingService(java.net.URL portAddress) throws ServiceException;
}
