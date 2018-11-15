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
	PROP_FILE=config/hygieia-nfrr-audit-collector.properties
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
dbname=${HYGIEIA_API_ENV_SPRING_DATA_MONGODB_DATABASE:-dashboarddb}

#Database HostName - default is localhost
dbhost=${MONGODB_HOST:-10.0.1.1}

#Database Port - default is 27017
dbport=${MONGODB_PORT:-27017}

#Database Username - default is blank
dbusername=${HYGIEIA_API_ENV_SPRING_DATA_MONGODB_USERNAME:-dashboarduser}

#Database Password - default is blank
dbpassword=${HYGIEIA_API_ENV_SPRING_DATA_MONGODB_PASSWORD:-dbpassword}

#Collector schedule (required)
nfrr.cron=${NFRR_CRON:-0 0/30 * * * *}

# Audit number of days - begin date
nfrr.days=${NFRR_DAYS:-30}

#nfrr server (required) - Can provide multiple
#nfrr.servers[0]=http://nfrr.company.com
#nfrr.environments[0]=[DEV,QA,INT,PERF,PROD]
#nfrr.apiKeys[0]=abcd1234

EOF

# find how many nfrr urls are configured
max=$(wc -w <<< "${!NFRR_URL*}")

# loop over and output the url, username, apiKey
i=0
while [ $i -lt $max ]
do
	if [ $i -eq 0 ]
	then
		server="NFRR_URL"
		username="NFRR_USERNAME"
		apiKey="NFRR_API_KEY"
		days="NFRR_DAYS"
	else
		server="NFRR_URL$i"
		username="NFRR_USERNAME$i"
		apiKey="NFRR_API_KEY$i"
		days="NFRR_DAYS$i"
	fi
	
cat >> $PROP_FILE <<EOF
nfrr.servers[${i}]=${!server}
nfrr.usernames[${i}]=${!username}
nfrr.apiKeys[${i}]=${!apiKey}
nfrr.days[${i}]=${!days}

EOF
	
	i=$(($i+1))
done

cat >> $PROP_FILE <<EOF
#Determines if audit console log is collected - defaults to false
nfrr.saveLog=${NFRR_SAVE_LOG:-true}
EOF

echo "

===========================================
Properties file created `date`:  $PROP_FILE
Note: passwords & apiKey hidden
===========================================
`cat $PROP_FILE |egrep -vi 'password|apiKey'`
"

exit 0
