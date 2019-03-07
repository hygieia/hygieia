package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.util.Supplier;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClientBuilder;


@Component
public class RestOperationsSupplier implements Supplier<RestOperations> {
     static String proxyValue;    //hold proxy 
     static  int proxyPort;       //proxy port value

     public static void set(String proxy,int port){    // get proxy settings
        proxyValue=proxy;
        proxyPort=port;
      }
  @Override
    public RestOperations get() {

    if(!"".equals(proxyValue)&&proxyValue!=null){                                      //check for proxy presence
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
        HttpClientBuilder.create()
                .setProxy(new HttpHost(proxyValue,proxyPort, "http"))
                .build());                                                             //set proxy
         requestFactory.setConnectTimeout(20000);
        requestFactory.setReadTimeout(20000);
        return new RestTemplate(requestFactory);

}else
{
 HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();   //no proxy condition
 requestFactory.setConnectTimeout(20000);
        requestFactory.setReadTimeout(20000);
        return new RestTemplate(requestFactory);

}
     }
}
