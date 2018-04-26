#!/bin/bash

if [ "$SKIP_PROPERTIES_BUILDER" = true ]; then
  echo "Skipping properties builder"
  exit 0
fi

if [ "$TEST_SCRIPT" != "" ]
then
        #for testing locally
        PROP_FILE=application.properties
else 
	PROP_FILE=config/hygieia-appdynamics-performance-collector.properties
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


cat > $PROP_FILE <<EOF
#Database Name - default is test
database=${SPRING_DATA_MONGODB_DATABASE:-dashboarddb}

#Database HostName - default is localhost
dbhost=${SPRING_DATA_MONGODB_HOST:-10.0.1.1}

#Database Port - default is 27017
dbport=${SPRING_DATA_MONGODB_PORT:-27017}

#Database Username - default is blank
dbusername=${HYGIEIA_API_ENV_SPRING_DATA_MONGODB_USERNAME:-dashboarduser}

#Database Password - default is blank
dbpassword=${HYGIEIA_API_ENV_SPRING_DATA_MONGODB_PASSWORD:-dbpassword}

#Logging File
logging.file=${APPDYNAMICS_LOGFILE:-./logs/appd-collector.log}

#Collector schedule (required)
appdynamics.cron=${APPDYNAMICS_CRON:-1 * * * * *}

#Appdynamics server (required)
appdynamics.instanceUrls=${APPDYNAMICS_INSTANCE_URL:-http://appdynamics}

#Appdynamics Username (required)
appdynamics.username=${APPDYNAMICS_USERNAME} # (if multi-tenancy APPD_USERNAME@TENANT)

#Appdynamics Password (required)
appdynamics.password=${APPDYNAMICS_PASSWORD}

#Appdynamics Dashboard (required)
appdynamics.dashboardUrl=${APPDYNAMICS_DASHBOARD_URL:-'http://appdynamics/controller/#/location=APP_DASHBOARD&timeRange=last_15_minutes.BEFORE_NOW.-1.-1.15&application=%s&dashboardMode=force'}

EOF
