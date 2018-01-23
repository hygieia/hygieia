---
title: Configure Proxy Authentication
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: proxyauthentication.html
---

Hygieia supports proxy authentication to work with corporate firewalls. You can implement proxy authentication for Hygieia with the following settings:

- Developer Settings
- Deployment/Operations Settings

## Developer Settings

Update the following properties in your Maven `\.m2\settings.xml` file:

```bash
...
<proxies>
       ...
       <proxy>
               <id>your-proxy-id</id>
               <active>true</active>
               <protocol>http</protocol>
               <host>your.proxy.domain.name</host>
               <port>8080</port>
               <!-- For authenticated proxy, set the following additional properties -->
               <username>companyId999</username>
               <password>yourPassword</password>
               <nonProxyHosts>*.local</nonProxyHosts>
       </proxy>
       ...
 </proxies>
...
```

In addition, set the export variables from the terminal/command prompt:

```bash
export HTTP_PROXY=http://companyId999:yourPassword@your.proxy.domain.name:8080
export HTTPS_PROXY=http://companyId999:yourPassword@your.proxy.domain.name:8080
export JAVA_OPTS="$JAVA_OPTS -Dhttp.proxyHost=your.proxy.domain.name -Dhttp.proxyPort=8080 -Dhttp.proxyUser=companyId999 -Dhttp.proxyPassword=yourPassword"
# This variable is a duplicate if you have already updated your Maven settings.xml file, but will only help:
export MAVEN_OPTS="$MAVEN_OPTS -Dhttp.proxyHost=your.proxy.domain.name -Dhttp.proxyPort=8080 -Dhttp.proxyUser=companyId999 -Dhttp.proxyPassword=yourPassword"
```

You can now run/pass test cases when you build the project from behind a corporate proxy, including authenticated proxy.

## Deployment/Operations Settings

To implement proxy authentication for your deployment instance, set the non-authentication proxy settings on the deployment instance from the terminal/command prompt:

```bash
export HTTP_PROXY=http://companyId999:yourPassword@your.proxy.domain.name:8080
export HTTPS_PROXY=http://companyId999:yourPassword@your.proxy.domain.name:8080
export JAVA_OPTS="$JAVA_OPTS -Dhttp.proxyHost=your.proxy.domain.name -Dhttp.proxyPort=8080 -Dhttp.proxyUser=companyId999 -Dhttp.proxyPassword=yourPassword"
# This variable is a duplicate if you have already updated your Maven settings.xml file, but will only help:
export MAVEN_OPTS="$MAVEN_OPTS -Dhttp.proxyHost=your.proxy.domain.name -Dhttp.proxyPort=8080 -Dhttp.proxyUser=companyId999 -Dhttp.proxyPassword=yourPassword"
```
In addition, update the property file for each collector/API configuration with their specific proxy setting property.
For information on setting the application properties file, refer to the sample [API Properties](api/api.md#api-properties-file) file.