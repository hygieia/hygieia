#!/bin/bash

if [ "$SKIP_PROPERTIES_BUILDER" = true ]; then
  echo "Skipping properties builder"
  exit 0
fi

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

if [ $SONAR_VERSION ]
then
	if [ $SONAR_METRICS ]
	then
		echo Sonar Version and Metrics explictly set
		echo if not set would default to config for Sonar earlier than version 6
	else
		if [[ $SONAR_VERSION -lt 6 ]]
		then
			SONAR_METRICS=ncloc,line_coverage,violations,critical_violations,major_violations,blocker_violations,sqale_index,test_success_density,test_failures,test_errors,tests
		else
			SONAR_METRICS=ncloc,violations,new_vulnerabilities,critical_violations,major_violations,blocker_violations,tests,test_success_density,test_errors,test_failures,coverage,line_coverage,sqale_index,alert_status,quality_gate_details

		fi
	fi
else
	#sonar.version defaults to sonar before v6
	SONAR_VERSION=1
	SONAR_METRICS=ncloc,line_coverage,violations,critical_violations,major_violations,blocker_violations,sqale_index,test_success_density,test_failures,test_errors,tests
fi
echo SONAR_VERSION: $SONAR_VERSION
echo SONAR_METRICS: $SONAR_METRICS

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
sonar.username=$SONAR_USERNAME

#Sonar Authentication Password - default is blank
sonar.password=$SONAR_PASSWORD

#Sonar Metrics
sonar.metrics[0]=${SONAR_METRICS:-ncloc,line_coverage,violations,critical_violations,major_violations,blocker_violations,sqale_index,test_success_density,test_failures,test_errors,tests}

#Sonar Version - see above for semantics between version/metrics
sonar.versions[0]=${SONAR_VERSION}

EOF

echo "

===========================================
Properties file created `date`:  $PROP_FILE
Note: passwords hidden
===========================================
`cat $PROP_FILE |egrep -vi password`
 "

exit 0
