#!/bin/bash

if [ "$SKIP_PROPERTIES_BUILDER" = true ]; then
  echo "Skipping properties builder"
  echo "`cat $PROP_FILE`"
  exit 0
fi

cat > $PROP_FILE <<EOF
dbname=${MONGODB_DATABASE:-dashboarddb}
dbhost=${MONGODB_HOST:-db}
dbport=${MONGODB_PORT:-27017}
dbusername=${MONGODB_USERNAME:-dashboarduser}
dbpassword=${MONGODB_PASSWORD:-dbpassword}

github.cron=${GITHUB_CRON:-0 */30 * * * *}
github.host=${GITHUB_HOST:-github.com}

github.firstRunHistoryDays=${GITHUB_FIRST_RUN_HISTORY_DAYS:-15}
github.errorThreshold=${GITHUB_ERROR_THRESHOLD:-1}

github.rateLimitThreshold=${GITHUB_RATE_LIMIT_THRESHOLD:-100}
github.personalAccessToken=${PERSONAL_ACCESS_TOKEN}
EOF

echo "
===========================================
Properties file created `date`:  $PROP_FILE
Note: passwords hidden
===========================================
`cat $PROP_FILE |egrep -vi password`
 "

exit 0
