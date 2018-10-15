package com.capitalone.dashboard.collector;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import com.capitalone.dashboard.util.Supplier;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Supplier that returns an instance of RestOperations
 */
@Component
@ConfigurationProperties(prefix = "jenkins")
public class RestOperationsSupplier implements Supplier<RestOperations> {
     private static final Logger LOG = LoggerFactory.getLogger(RestOperationsSupplier.class);

    // Provide default value for injected fields
    private Integer socketConnectTimeoutMS = 20000;
    private Integer socketReadTimeoutMS    = 20000;

    @Override
    public RestOperations get() {

        HttpComponentsClientHttpRequestFactory requestFactory =
            new HttpComponentsClientHttpRequestFactory();

        requestFactory.setConnectTimeout(getSocketConnectTimeoutMS());

        requestFactory.setReadTimeout(getSocketReadTimeoutMS());

        return new RestTemplate(requestFactory);
    }

    public Integer getSocketConnectTimeoutMS()
    {
        LOG.info("getSocketConnectTimeoutMS:  " + socketConnectTimeoutMS);
        return(this.socketConnectTimeoutMS);
    }

    public void setSocketConnectTimeoutMS(Integer socketConnectTimeoutMS)
    {
        LOG.info("setSocketConnectTimeoutMS:  " + socketConnectTimeoutMS);
        this.socketConnectTimeoutMS = socketConnectTimeoutMS;
    }

    public Integer getSocketReadTimeoutMS()
    {
        LOG.info("getSocketReadTimeoutMS:  " + socketReadTimeoutMS);
        return(this.socketReadTimeoutMS);
    }

    public void setSocketReadTimeoutMS(Integer socketReadTimeoutMS)
    {
        LOG.info("setSocketReadTimeoutMS:  " + socketReadTimeoutMS);
        this.socketReadTimeoutMS = socketReadTimeoutMS;
    }
}
