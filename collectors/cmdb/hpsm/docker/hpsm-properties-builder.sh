#!/bin/bash

# mongo container provides the HOST/PORT
# api container provided DB Name, ID & PWD

if [ "$TEST_SCRIPT" != "" ]
then
        #for testing locally
        PROP_FILE=application.properties
else 
	PROP_FILE=hpsm.properties
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
hpsm.cron=${HPSM_CRON:-* * 1 * * *}

#Api Details
hpsm.server=${HPSM_SERVER:}
hpsm.port=${HPSM_PORT:}
hpsm.protocol=${HPSM_PROTOCOL:http}
hpsm.resource=${HPSM_RESOURCE:SM/7/ws/}
hpsm.contentType=${HPSM_CONTENTTYPE:text/xml}
hpsm.charset=${HPSM_CHARSET:UTF-8}

#Api User/ Pass
hpsm.user=${HPSM_USER:}
hpsm.pass=${HPSM_PASS:}

#Api App Query settings
hpsm.appSubType=${HPSM_APP_SUBTYPE:}
hpsm.appType=${HPSM_APP_TYPE:}
hpsm.appStatus=${HPSM_APP_STATUS:}

#Api Component Query settings
hpsm.compSubType=${HPSM_COMP_SUBTYPE:}
hpsm.compType=${HPSM_COMP_TYPE:}

#API app details
hpsm.detailsRequestType=${HPSM_REQUEST_TYPE:RetrieveDeviceListRequest}
hpsm.detailsSoapAction=${HPSM_SOAP_ACTION:RetrieveList}

EOF

echo "

===========================================
Properties file created `date`:  $PROP_FILE
Note: passwords hidden
===========================================
`cat $PROP_FILE |egrep -vi password`
 "

exit 0
