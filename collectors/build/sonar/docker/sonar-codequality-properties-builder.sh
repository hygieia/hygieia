#!/bin/bash

# mongo container provides the HOST/PORT
# api container provided DB Name, ID & PWD

if [ "$TEST_SCRIPT" != "" ]
then
        #for testing locally
        PROP_FILE=application.properties
else
	PROP_FILE=hygieia-sonar-codequality-collector.properties
fi

if [ "$MONGO_PORT" != "" ]; then
	# Sample: MONGO_PORT=tcp://172.17.0.20:27017
	MONGODB_HOST=`echo $MONGO_PORT|sed 's;.*://\([^:]*\):\(.*\);\1;'`
	MONGODB_PORT=`echo $MONGO_PORT|sed 's;.*://\([^:]*\):\(.*\);\2;'`
else
	env
	echo "ERROR: MONGO_PORT not defined"
	exit 1
fi

echo "MONGODB_HOST: $MONGODB_HOST"
echo "MONGODB_PORT: $MONGODB_PORT"

#update local host to bridge ip if used for a URL
SONAR_LOCALHOST=
echo $SONAR_URL|egrep localhost >>/dev/null
if [ $? -ne 1 ]
then
        #this seems to give a access to the VM of the docker-machine
        #LOCALHOST=`ip route|egrep '^default via'|cut -f3 -d' '`
        #see http://superuser.com/questions/144453/virtualbox-guest-os-accessing-local-server-on-host-os
        SONAR_LOCALHOST=10.0.2.2
        MAPPED_URL=`echo "$SONAR_URL"|sed "s|localhost|$SONAR_LOCALHOST|"`
        echo "Mapping localhost -> $MAPPED_URL"
        SONAR_URL=$MAPPED_URL
fi

cat > $PROP_FILE <<EOF
#Database Name
dbname=${HYGIEIA_API_ENV_SPRING_DATA_MONGODB_DATABASE:-dashboard}

#Database HostName - default is localhost
dbhost=${MONGODB_HOST:-10.0.1.1}

#Database Port - default is 27017
dbport=${MONGODB_PORT:-27017}

#Database Username - default is blank
dbusername=${HYGIEIA_API_ENV_SPRING_DATA_MONGODB_USERNAME:-db}

#Database Password - default is blank
dbpassword=${HYGIEIA_API_ENV_SPRING_DATA_MONGODB_PASSWORD:-dbpass}

#Collector schedule (required)
sonar.cron=${SONAR_CRON:-0 0/5 * * * *}

sonar.servers[0]=${SONAR_URL:-http://localhost:9000}

#Sonar Authentication Username - default is blank
sonar.username=${SONAR_USERNAME:-sonarusername}

#Sonar Authentication Password - default is blank
sonar.password=${SONAR_PASSWORD:-sonarpassword}

#Sonar Metrics
sonar.metrics=${SONAR_METRICS:-ncloc,line_coverage,violations,critical_violations,major_violations,blocker_violations,sqale_index,test_success_density,test_failures,test_errors,tests}

EOF

echo "

===========================================
Properties file created `date`:  $PROP_FILE
Note: passwords hidden
===========================================
`cat $PROP_FILE |egrep -vi password`
 "

exit 0
