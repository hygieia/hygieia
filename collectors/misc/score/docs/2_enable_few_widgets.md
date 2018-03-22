# Enable few widgets settings

Enable 3 widgets : github, build, quality
```properties

#Max score to display as 5
score.maxScore=5
#If value is equal or less to 0 show alert for the widget
score.widgetAlert.value=0
#If widget is not found, set value as no score to ignore widget in score calculation
score.criteria.noWidgetFound.scoreType=no_score
#If widget has no data, score would be 0 for it
score.criteria.noDataFound.scoreType=zero_score

#Weight for github widget
score.scmWidget.weight=34

#Weight for build widget
score.buildWidget.weight=33

#Weight for quality widget
score.qualityWidget.weight=33
```
