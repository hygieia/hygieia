#!/bin/bash

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
EOF
