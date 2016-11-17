#!/bin/bash

# mongo container provides the HOST/PORT
# api container provided DB Name, ID & PWD

if [ "$TEST_SCRIPT" != "" ]
then
        #for testing locally
        PROP_FILE=application.properties
else 
	PROP_FILE=hygieia-xldeploy-deployment-collector.properties
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
xldeploy.cron=${XLDEPLOY_CRON:-0 0/5 * * * *}
EOF

echo -e "\n#XLDeploy server (required) - Can provide multiple" >> $PROP_FILE
idx=0
for x in ${!XLDEPLOY_URL*}
do
	echo "xldeploy.servers[$idx]=${!x:-http://xldeploy.company.com}" >> $PROP_FILE
	idx=$((idx+1))
done

echo -e "\n#XLDeploy server names (Optional) - Can provide multiple" >> $PROP_FILE
idx=0
for x in ${!XLDEPLOY_NAME*}
do
	echo "xldeploy.niceNames[$idx]=${!x:-XLDeploy}" >> $PROP_FILE
	idx=$((idx+1))
done

echo -e "\n#XLDeploy user name (required) - Can provide multiple" >> $PROP_FILE
idx=0
for x in ${!XLDEPLOY_USERNAME*}
do
	echo "xldeploy.usernames[$idx]=${!x:-bobama}" >> $PROP_FILE
	idx=$((idx+1))
done

echo -e "\n#XLDeploy password (required) - Can provide multiple" >> $PROP_FILE
idx=0
for x in ${!XLDEPLOY_PASSWORD*}
do
	echo "xldeploy.passwords[$idx]=${!x:-s3cr3t}" >> $PROP_FILE
	idx=$((idx+1))
done

echo "

===========================================
Properties file created `date`:  $PROP_FILE
Note: passwords hidden
===========================================
`cat $PROP_FILE |egrep -vi password`
 "

exit 0
