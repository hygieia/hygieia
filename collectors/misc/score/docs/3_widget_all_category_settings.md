# Widget all category settings

Detailed settings for all widgets and its categories : github, build, quality, deploy
```properties

#Max score to display as 5
score.maxScore=5
#If value is equal or less to 0 show alert for the widget
score.componentAlert.value=0
#If Widget is not present in dashboard, score would be 0 for it
score.criteria.noWidgetFound.scoreType=zero_score
#If widget has no data, score would be 0 for it
score.criteria.noDataFound.scoreType=zero_score

#Weight for github widget
score.scmWidget.weight=25
score.scmWidget.disabled=false
#Number of days for commits
score.scmWidget.numberOfDays=14

#Add threshold criteria to check if there is no commit in last 7 days, set score as 0
score.scmWidget.criteria.dataRangeThresholds[0].type=days
score.scmWidget.criteria.dataRangeThresholds[0].comparator=equals
score.scmWidget.criteria.dataRangeThresholds[0].value=0
score.scmWidget.criteria.dataRangeThresholds[0].numDaysToCheck=7
score.scmWidget.criteria.dataRangeThresholds[0].score.scoreType=zero_score
score.scmWidget.criteria.dataRangeThresholds[0].score.propagate=no

#Add threshold criteria to check if there are less than 25% commits present, set score as 0
score.scmWidget.criteria.dataRangeThresholds[1].type=percent
score.scmWidget.criteria.dataRangeThresholds[1].comparator=less
score.scmWidget.criteria.dataRangeThresholds[1].value=25
score.scmWidget.criteria.dataRangeThresholds[1].score.scoreType=zero_score
score.scmWidget.criteria.dataRangeThresholds[1].score.propagate=no

# Commits per day widget criteria settings
score.scmWidget.commitsPerDay.weight=100
score.scmWidget.commitsPerDay.numberOfDays=14


#Add threshold criteria to check if there are more than 60% commits present, set score as 100
score.scmWidget.commitsPerDay.criteria.dataRangeThresholds[0].type=percent
score.scmWidget.commitsPerDay.criteria.dataRangeThresholds[0].comparator=greater_or_equal
score.scmWidget.commitsPerDay.criteria.dataRangeThresholds[0].value=60
score.scmWidget.commitsPerDay.criteria.dataRangeThresholds[0].score.scoreType=value_percent
score.scmWidget.commitsPerDay.criteria.dataRangeThresholds[0].score.scoreValue=100
score.scmWidget.commitsPerDay.criteria.dataRangeThresholds[0].score.propagate=no

score.buildWidget.numberOfDays=14
score.buildWidget.weight=25

#Add threshold criteria to check if there is no build in last 7 days, set score as 0
score.buildWidget.criteria.dataRangeThresholds[0].type=days
score.buildWidget.criteria.dataRangeThresholds[0].comparator=equals
score.buildWidget.criteria.dataRangeThresholds[0].value=0
score.buildWidget.criteria.dataRangeThresholds[0].numDaysToCheck=7
score.buildWidget.criteria.dataRangeThresholds[0].score.scoreType=zero_score
score.buildWidget.criteria.dataRangeThresholds[0].score.propagate=no

#Add threshold criteria to check if there are less than 25% builds present, set score as 0
score.buildWidget.criteria.dataRangeThresholds[1].type=percent
score.buildWidget.criteria.dataRangeThresholds[1].comparator=less
score.buildWidget.criteria.dataRangeThresholds[1].value=25
score.buildWidget.criteria.dataRangeThresholds[1].score.scoreType=zero_score
score.buildWidget.criteria.dataRangeThresholds[1].score.propagate=no

score.qualityWidget.weight=25

# Code Coverage
score.qualityWidget.codeCoverage.weight=34
# If no code coverage found set quality score as 0 for Quality Widget
score.qualityWidget.codeCoverage.criteria.noDataFound.scoreType=zero_score
score.qualityWidget.codeCoverage.criteria.noDataFound.scoreValue=0
score.qualityWidget.codeCoverage.criteria.noDataFound.propagate=widget
# If data is less than equal to 50% for Code Coverage set score as 0 for Code Coverage
score.qualityWidget.codeCoverage.criteria.dataRangeThresholds[0].type=percent
score.qualityWidget.codeCoverage.criteria.dataRangeThresholds[0].comparator=less
score.qualityWidget.codeCoverage.criteria.dataRangeThresholds[0].value=50
score.qualityWidget.codeCoverage.criteria.dataRangeThresholds[0].score.scoreType=zero_score
score.qualityWidget.codeCoverage.criteria.dataRangeThresholds[0].score.propagate=no

# Unit Tests
score.qualityWidget.unitTests.weight=33
# If no unit tests data found set quality score as 0 for Quality Widget
score.qualityWidget.unitTests.criteria.noDataFound.scoreType=zero_score
score.qualityWidget.unitTests.criteria.noDataFound.scoreValue=0
score.qualityWidget.unitTests.criteria.noDataFound.propagate=widget
# If data is less than equal to 100% for Unit Tests set score as 0 for unit tests
score.qualityWidget.unitTests.criteria.dataRangeThresholds[0].type=percent
score.qualityWidget.unitTests.criteria.dataRangeThresholds[0].comparator=less
score.qualityWidget.unitTests.criteria.dataRangeThresholds[0].value=100
score.qualityWidget.unitTests.criteria.dataRangeThresholds[0].score.scoreType=zero_score
score.qualityWidget.unitTests.criteria.dataRangeThresholds[0].score.propagate=no

# Violations
score.qualityWidget.violations.weight=33
# For calculating violations formula
# 100 - ((blockers * 20) + (critical * 5) + (major * 1))
score.qualityWidget.violations.blockerViolationsWeight=20
score.qualityWidget.violations.criticalViolationsWeight=5
score.qualityWidget.violations.majorViolationWeight=1
# If violation score is less than 0, set it as 0
score.qualityWidget.violations.allowNegative=false

# If no violations data found set score as No Score for violations
score.qualityWidget.violations.criteria.noDataFound.scoreType=no_score
score.qualityWidget.violations.criteria.noDataFound.propagate=no

score.deployWidget.weight=25
score.deployWidget.disabled=false

# If deploy widget is not found, set value as no score to ignore widget in score calculation
score.deployWidget.criteria.noWidgetFound.scoreType=no_score
score.deployWidget.criteria.noWidgetFound.propagate=no

# Deploy Success Settings
score.deployWidget.deploySuccess.weight=50
score.deployWidget.deploySuccess.disabled=false
# If no data found set score for widget as 0
score.deployWidget.deploySuccess.criteria.noDataFound.scoreType=zero_score
score.deployWidget.deploySuccess.criteria.noDataFound.propagate=widget

# Instances OnlineSettings
score.deployWidget.intancesOnline.weight=50
score.deployWidget.intancesOnline.disabled=false
# If no data found set score for widget as 0
score.deployWidget.intancesOnline.criteria.noDataFound.scoreType=zero_score
score.deployWidget.intancesOnline.criteria.noDataFound.propagate=widget
```
