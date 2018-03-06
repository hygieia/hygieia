# Default settings

Default settings for all widgets : github, build, quality, deploy
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

#Weight for build widget
score.buildWidget.weight=25

#Weight for quality widget
score.qualityWidget.weight=25

#Weight for deploy widget
score.deployWidget.weight=25
```
