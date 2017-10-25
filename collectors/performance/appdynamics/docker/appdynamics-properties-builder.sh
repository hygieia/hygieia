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

cat > appdynamics.properties <<EOF
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

#Logging File
logging.file=${APPDYNAMICS_LOGFILE:-./logs/appd-collector.log}

#Collector schedule (required)
appdynamics.cron=${APPDYNAMICS_CRON:-1 * * * * *}

#Appdynamics server (required)
appdynamics.instanceUrl=${APPDYNAMICS_INSTANCE_URL:-http://appdynamics}

#Appdynamics Username (required)
appdynamics.username=${APPDYNAMICS_USERNAME} # (if multi-tenancy APPD_USERNAME@TENANT)

#Appdynamics Password (required)
appdynamics.password=${APPDYNAMICS_PASSWORD}

#Appdynamics Dashboard (required)
appdynamics.dashboardUrl=${APPDYNAMICS_DASHBOARD_URL:-'http://appdynamics/controller/#/location=APP_DASHBOARD&timeRange=last_15_minutes.BEFORE_NOW.-1.-1.15&application=%s&dashboardMode=force'}

EOF
