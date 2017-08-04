---
title: Configure Proxy Authentication
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: proxyauthentication.html
---
## Configure Proxy

Hygieia supports proxy authentication for working behind corporate firewalls.  For development, please refer to the following configuration differences; for deployment/operations, please refer to the subsequent sub-section:

## Proxy Config: Developer

Update your Maven settings.xml file:

```
...
<proxies>
       ...
       <proxy>
               <id>your-proxy-id</id>
               <active>true</active>
               <protocol>http</protocol>
               <host>your.proxy.domain.name</host>
               <port>8080</port>
               <!-- For authenticated proxy, please set the following, as well -->
               <username>companyId999</username>
               <password>yourPassword</password>
               <nonProxyHosts>*.local</nonProxyHosts>
       </proxy>
       ...
 </proxies>
...
```

Additionally, set the following export variables:

```bash
export HTTP_PROXY=http://companyId999:yourPassword@your.proxy.domain.name:8080
export HTTPS_PROXY=http://companyId999:yourPassword@your.proxy.domain.name:8080
export JAVA_OPTS="$JAVA_OPTS -Dhttp.proxyHost=your.proxy.domain.name -Dhttp.proxyPort=8080 -Dhttp.proxyUser=companyId999 -Dhttp.proxyPassword=yourPassword"
# This option may be duplicative if you have already updated your
# Maven settings.xml file, but will only help:
export MAVEN_OPTS="$MAVEN_OPTS -Dhttp.proxyHost=your.proxy.domain.name -Dhttp.proxyPort=8080 -Dhttp.proxyUser=companyId999 -Dhttp.proxyPassword=yourPassword"
```

Tests should now run/pass when built from behind a corporate proxy, even if it is an authenticated proxy

## Proxy Config: Deployment / Operations

Only the above proxy settings (non-authentication) may required to be set on your deployment instance.  Additionally, please update all property files for each collector/API configuration with their specific proxy setting property.