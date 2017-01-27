#!/bin/bash

if [ "$TEST_SCRIPT" != "" ]
then
    #for testing locally
    PROP_FILE=application.properties
else 
	PROP_FILE=hygieia-collector-config-server.properties
fi

cat > $PROP_FILE <<EOF

server.port=${CONFIG_SERVER_PORT:-8888}

spring.cloud.config.server.git.uri=${CONFIG_SERVER_GIT_URI:-}
spring.cloud.config.server.git.username=${CONFIG_SERVER_GIT_USERNAME:-}
spring.cloud.config.server.git.password=${CONFIG_SERVER_GIT_PASSWORD:-}

EOF
echo "
===========================================
Properties file created `date`:  $PROP_FILE
===========================================
`cat $PROP_FILE |egrep -vi password`
 "
exit 0