#!/bin/bash

if [ "$SKIP_PROPERTIES_BUILDER" = true ]; then
  echo "Skipping properties builder"
  exit 0
fi

# if we are linked, use that info
if [ "$MONGO_STARTED" != "" ]; then
  # links now use hostnames
  # todo: retrieve linked information such as hostname and port exposition
  export SPRING_DATA_MONGODB_HOST=mongodb
  export SPRING_DATA_MONGODB_PORT=27017
fi

echo "SPRING_DATA_MONGODB_HOST: $SPRING_DATA_MONGODB_HOST"
echo "SPRING_DATA_MONGODB_PORT: $SPRING_DATA_MONGODB_PORT"


cat > dashboard.properties <<EOF
#Database Name - default is test
dbname=${SPRING_DATA_MONGODB_DATABASE:-dashboard}

#Database HostName - default is localhost
dbhost=${SPRING_DATA_MONGODB_HOST:-10.0.1.1}

#Database Port - default is 27017
dbport=${SPRING_DATA_MONGODB_PORT:-9999}

#Database Username - default is blank
dbusername=${SPRING_DATA_MONGODB_USERNAME:-db}

#Database Password - default is blank
dbpassword=${SPRING_DATA_MONGODB_PASSWORD:-dbpass}

logRequest=${LOG_REQUEST:-false}
logSplunkRequest=${LOG_SPLUNK_REQUEST:-false}

corsEnabled=${CORS_ENABLED:-false}

corsWhitelist=${CORS_WHITELIST:-http://domain1.com:port,http://domain2.com:port}

feature.dynamicPipeline=${FEATURE_DYNAMIC_PIPELINE:-disabled}

#Authentication Settings
# JWT expiration time in milliseconds
auth.expirationTime=${AUTH_EXPIRATION_TIME:-}
# Secret Key used to validate the JWT tokens
auth.secret=${AUTH_SECRET:-}
auth.authenticationProviders=${AUTH_AUTHENTICATION_PROVIDERS:-}

# LDAP Server Url, including port of your LDAP server
auth.ldapServerUrl=${AUTH_LDAP_SERVER_URL:-}

# If using standard ldap
# LDAP User Dn Pattern, where the username is replaced with '{0}'
auth.ldapUserDnPattern=${AUTH_LDAP_USER_DN_PATTERN:-}

# If using ActiveDirectory
# This will be the domain part of your userPrincipalName
auth.adDomain=${AUTH_AD_DOMAIN:-}
# This will be your root dn
auth.adRootDn=${AUTH_AD_ROOT_DN:-}
# This is your active directory url
auth.adUrl=${AUTH_AD_URL:-}

# Needed if you want to query ldap
auth.ldapBindUser=${AUTH_LDAP_BIND_USER:-}
auth.ldapBindPass=${AUTH_LDAP_BIND_PASS:-}

#Monitor Widget proxy credentials
monitor.proxy.username=${MONITOR_PROXY_USERNAME:-}
monitor.proxy.password=${MONITOR_PROXY_PASSWORD:-}

#Monitor Widget proxy information
monitor.proxy.type=${MONITOR_PROXY_TYPE:-http}
monitor.proxy.host=${MONITOR_PROXY_HOST:-}
monitor.proxy.port=${MONITOR_PROXY_PORT:-80}

EOF
