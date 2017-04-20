## Hygieia New Features Released ##

### Basic Http Monitoring ###
 Hygieia Monitoring widget now supports basic http monitoring.
 
 
 To use this feature use the CapOne template. The widget is very simple to configure, you just need to add the url of the services you need to monitor in the format and provide a friendly name to it.
 The widget also supports listing dependent services and monitoring them. So if a dependent services is already configured on a another dashboard you can just declare them as dependent services.
 
 `http://samplewebsite.com or https://samplewebsite.com`
 
 Please do remember that if you are using a self signed certificate you would run into following error
 
 `2016-11-14 17:08:25,452 ERROR c.c.d.service.ServiceServiceImpl - https://abc1234.com failed with sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target`
 
 to resolve this please import the certificate into the JVM running your api using java keytool. 
 
 Also at this time, the monitoring widget does not supports any proxy configuration.
 
 