#!/bin/bash

# mongo container provides the HOST/PORT
# api container provided DB Name, ID & PWD

if [ "$TEST_SCRIPT" != "" ]
then
        #for testing locally
        PROP_FILE=application.properties
else 
	PROP_FILE=hygieia-ca-apm-collector.properties
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
dbname=dashboard

#Database HostName - default is localhost
dbhost=mongo

#Database Port - default is 27017
dbport=27017

#Database Username - default is blank
dbusername=hygieia

#Database Password - default is blank
dbpassword=hygieia

#Collector schedule
CaApm.cron= 0 0/1 * * * *
CaApm.alertWsdl=https://company:port/introscope-web-services/services/AlertPollingService?wsdl
CaApm.modelWsdl=https://company:port/introscope-web-services/services/MetricGroupService?wsdl
CaApm.user=
CaApm.password=
EOF

echo "

===========================================
Properties file created `date`:  $PROP_FILE
Note: passwords hidden
===========================================
`cat $PROP_FILE |egrep -vi password`
 "

exit 0
