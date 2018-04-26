#!/bin/bash

if [ "$SKIP_PROPERTIES_BUILDER" = true ]; then
  echo "Skipping properties builder"
  echo "`cat $PROP_FILE`"
  exit 0
fi

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
score.cron=${SCORE_CRON:-0 0/5 * * * *}

# Max Score
score.maxScore=5

# Default Score Criteria for widget not found :
#   no_score : the widget will not be used to score
#   zero_score : the widget score will be 0
#   valuePercent : specify the value to set in scoreValue param (Default)
score.criteria.noWidgetFound.scoreType=zero_score
# When scoreType is valuePercent we need to define the value for score
score.criteria.noWidgetFound.scoreValue=0

# Default Score Criteria for data not found :
#   no_score : the value will not be used to score
#   zero_score : the score value will be 0
#   valuePercent : specify the value to set in scoreValue param (Default)
score.criteria.noDataFound.scoreType=zero_score
# When scoreType is valuePercent we need to define the value for score
score.criteria.noDataFound.scoreValue=0


# Score settings for build widget
# Number of days to calculate score
score.buildWidget.numberOfDays=14
# Weight for the widget
score.buildWidget.weight=25
# If widget is disabled it will not be used for calculating score
score.buildWidget.disabled=false

# Criteria properties, these will override the default criteria properties
score.buildWidget.criteria.noWidgetFound.scoreType=zero_score
score.buildWidget.criteria.noWidgetFound.scoreValue=0
# Property to propagate score if condition is met
#   no : do not propagate score
#   widget : propagate to widget
#   dashboard : propagate score as dashboard score
score.buildWidget.criteria.noWidgetFound.propagate=no

score.buildWidget.criteria.noDataFound.scoreType=zero_score
score.buildWidget.criteria.noDataFound.scoreValue=0
score.buildWidget.criteria.noDataFound.propagate=no

# Criteria thresholds for data within the range of days
# Type of threshold to apply
#   percent : percent of data
#   days : number of days where data is present
score.buildWidget.criteria.dataRangeThresholds[0].type=percent

# Comparator to compare the value
#   equals,
#   less,
#   greater,
#   less_or_equal,
#   greater_or_equal
score.buildWidget.criteria.dataRangeThresholds[0].comparator=less_or_equal

# Value to compare
score.buildWidget.criteria.dataRangeThresholds[0].value=20

# If the threshold is met set the score
score.buildWidget.criteria.dataRangeThresholds[0].score.scoreType=zero_score
score.buildWidget.criteria.dataRangeThresholds[0].score.scoreValue=0
score.buildWidget.criteria.dataRangeThresholds[0].score.propagate=no

# we can set the last n number of days to check for threshold (Not Mandatory)
score.buildWidget.criteria.dataRangeThresholds[0].numDaysToCheck=7

# Build duration within threshold score settings
score.buildWidget.duration.buildDurationThresholdInMillis=300000
score.buildWidget.duration.weight=50
score.buildWidget.duration.disabled=false

# Build duration within threshold score settings
score.buildWidget.status.weight=50
score.buildWidget.status.disabled=false

# Score settings for quality widget
score.qualityWidget.weight=25
score.qualityWidget.disabled=false

score.qualityWidget.criteria.noWidgetFound.scoreType=zero_score
score.qualityWidget.criteria.noWidgetFound.scoreValue=0
score.qualityWidget.criteria.noWidgetFound.propagate=no

score.qualityWidget.criteria.noDataFound.scoreType=zero_score
score.qualityWidget.criteria.noDataFound.scoreValue=0
score.qualityWidget.criteria.noDataFound.propagate=no

# Code Quality widget criteria settings
score.qualityWidget.codeCoverage.weight=50
score.qualityWidget.codeCoverage.disabled=false
score.qualityWidget.codeCoverage.criteria.noDataFound.scoreType=zero_score
score.qualityWidget.codeCoverage.criteria.noDataFound.scoreValue=0
score.qualityWidget.codeCoverage.criteria.noDataFound.propagate=widget


# Unit Tests widget criteria settings
score.qualityWidget.unitTests.weight=50
score.qualityWidget.unitTests.disabled=false
score.qualityWidget.unitTests.criteria.noDataFound.scoreType=zero_score
score.qualityWidget.unitTests.criteria.noDataFound.scoreValue=0
score.qualityWidget.unitTests.criteria.noDataFound.propagate=widget
score.qualityWidget.unitTests.criteria.dataRangeThresholds[0].type=percent
score.qualityWidget.unitTests.criteria.dataRangeThresholds[0].comparator=less
score.qualityWidget.unitTests.criteria.dataRangeThresholds[0].value=100
score.qualityWidget.unitTests.criteria.dataRangeThresholds[0].score.scoreType=zero_score
score.qualityWidget.unitTests.criteria.dataRangeThresholds[0].score.scoreValue=0
score.qualityWidget.unitTests.criteria.dataRangeThresholds[0].score.propagate=widget
# Score settings for deploy widget
score.deployWidget.weight=25
score.deployWidget.disabled=false

score.deployWidget.criteria.noWidgetFound.scoreType=zero_score
score.deployWidget.criteria.noWidgetFound.scoreValue=0
score.deployWidget.criteria.noWidgetFound.propagate=no

score.deployWidget.criteria.noDataFound.scoreType=zero_score
score.deployWidget.criteria.noDataFound.scoreValue=0
score.deployWidget.criteria.noDataFound.propagate=no

# Deployment widget criteria settings
score.deployWidget.deploySuccess.weight=50
score.deployWidget.deploySuccess.disabled=false
score.deployWidget.deploySuccess.criteria.noDataFound.scoreType=zero_score
score.deployWidget.deploySuccess.criteria.noDataFound.scoreValue=0
score.deployWidget.deploySuccess.criteria.noDataFound.propagate=widget


# Instances online widget criteria settings
score.deployWidget.intancesOnline.weight=50
score.deployWidget.intancesOnline.disabled=false
score.deployWidget.intancesOnline.criteria.noDataFound.scoreType=zero_score
score.deployWidget.intancesOnline.criteria.noDataFound.scoreValue=0
score.deployWidget.intancesOnline.criteria.noDataFound.propagate=widget


# Score settings for github scm widget
score.scmWidget.weight=25
score.scmWidget.disabled=false
score.scmWidget.numberOfDays=14

score.scmWidget.criteria.noWidgetFound.scoreType=zero_score
score.scmWidget.criteria.noWidgetFound.scoreValue=0
score.scmWidget.criteria.noWidgetFound.propagate=no

score.scmWidget.criteria.noDataFound.scoreType=zero_score
score.scmWidget.criteria.noDataFound.scoreValue=0
score.scmWidget.criteria.noDataFound.propagate=no

score.scmWidget.criteria.dataRangeThresholds[0].type=percent
score.scmWidget.criteria.dataRangeThresholds[0].comparator=less_or_equal

# Value to compare
score.scmWidget.criteria.dataRangeThresholds[0].value=20

# If the threshold is met set the score
score.scmWidget.criteria.dataRangeThresholds[0].score.scoreType=zero_score
score.scmWidget.criteria.dataRangeThresholds[0].score.scoreValue=0
score.scmWidget.criteria.dataRangeThresholds[0].score.propagate=no

# we can set the last n number of days to check for threshold (Not Mandatory)
score.scmWidget.criteria.dataRangeThresholds[0].numDaysToCheck=7


# Commits per day widget criteria settings
score.scmWidget.commitsPerDay.weight=100
score.scmWidget.commitsPerDay.numberOfDays=14
score.scmWidget.commitsPerDay.disabled=false
score.scmWidget.commitsPerDay.criteria.noDataFound.scoreType=zero_score
score.scmWidget.commitsPerDay.criteria.noDataFound.scoreValue=0
score.scmWidget.commitsPerDay.criteria.noDataFound.propagate=widget


score.scmWidget.commitsPerDay.criteria.dataRangeThresholds[0].type=percent
score.scmWidget.commitsPerDay.criteria.dataRangeThresholds[0].comparator=less_or_equal
score.scmWidget.commitsPerDay.criteria.dataRangeThresholds[0].value=20
score.scmWidget.commitsPerDay.criteria.dataRangeThresholds[0].score.scoreType=zero_score
score.scmWidget.commitsPerDay.criteria.dataRangeThresholds[0].score.scoreValue=0
score.scmWidget.commitsPerDay.criteria.dataRangeThresholds[0].score.propagate=widget
score.scmWidget.commitsPerDay.criteria.dataRangeThresholds[0].numDaysToCheck=7

EOF

echo "

===========================================
Properties file created `date`:  $PROP_FILE
Note: passwords hidden
===========================================
`cat $PROP_FILE |egrep -vi password`
 "

exit 0
