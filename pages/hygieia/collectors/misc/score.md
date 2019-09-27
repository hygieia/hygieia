---
title: Score Collector
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: score.html
---

Configure the score collector to view star ratings for team dashboards, based on the following widgets:
- SCM
- Build
- Deploy
- Code Quality

Hygieia uses Spring Boot to package the collector as an executable JAR file with dependencies.

### Setup Instructions

## Fork and Clone the Collector 

Fork and clone the Score Collector from the [GitHub repo](https://github.com/Hygieia/hygieia-misc-score-collector). 

To configure the Score Collector, execute the following steps:

*   **Step 1: Change Directory**

Change the current working directory to the `hygieia-misc-score-collector` directory of your Hygieia source code installation.

For example, in the Windows command prompt, run the following command:

```
cd C:\Users\[username]\hygieia-misc-score-collector
```

*   **Step 2: Run Maven Build**

Run the maven build to package the collector into an executable JAR file:

```bash
mvn install
```

The output file `[collector name].jar` is generated in the `hygieia-misc-score-collector\target` folder.

*   **Step 3: Set Parameters in Application Properties File**

Set the configurable parameters in the `application.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the Score Collector.

To configure parameters for the Score Collector, refer to the sample [application.properties](#sample-application-properties-file) file.

For information about sourcing the application properties file, refer to the [Spring Boot Documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*   **Step 4: Deploy the Executable File**

To deploy the `[collector name].jar` file, change directory to `hygieia-misc-score-collector\target`, and then execute the following from the command prompt:

```bash
java -jar [collector name].jar --spring.config.name=score --spring.config.location=[path to application.properties file]
``` 

## Sample Application Properties File

The sample `application.properties` file lists parameters with sample values to configure the Score Collector. In addition, this file contains information about connecting to the Dashboard MongoDB database instance. Set the parameters based on your environment setup.

### Database Connection and Common properties

```properties
# Database Name
dbname=dashboarddb

# Database HostName - default is localhost
dbhost=localhost

# Database Port - default is 27017
dbport=27017

# MongoDB replicaset
dbreplicaset=[false if you are not using MongoDB replicaset]
dbhostport=[host1:port1,host2:port2,host3:port3]

# Database Username - default is blank
dbusername=dashboarduser

# Database Password - default is blank
dbpassword=dbpassword

# Collector schedule (required)
score.cron=0 0/5 * * * *

# Max Score
score.maxScore=5
```

#### Common Score Calculation Criteria

Default criteria can be set at top level and it will apply to all the child widgets unless it is overriden
 - **Widget Not Found Criteria**: When a widget is not found, we can choose to set no score, set zero score, or set a custom score for that widget
    - Example for no score. When a widget has no score, it would not be considered while calculating score for the dashboard
        ```properties
        #   no_score : the widget will not be used to score
        score.criteria.noWidgetFound.scoreType=no_score
        ```
    - Example for zero score 
        ```properties
        #   zero_score : the score value will be 0
        score.criteria.noWidgetFound.scoreType=zero_score
        ```
    - Example for custom score. We can choose this option to set a custom score percent value
        ```properties
        #   valuePercent : specify the value to set in scoreValue param
        score.criteria.noWidgetFound.scoreType=valuePercent
        # When scoreType is valuePercent we need to define the value for score
        score.criteria.noWidgetFound.scoreValue=20
        ```  
 - **Data Not Found Criteria**: When a widget is present but has no data, we can choose to set no score, set zero score, or set a custom score for that widget
    - Example for no score. When a widget has no score, it would not be considered while calculating score for the dashboard
        ```properties
        #   no_score : the widget will not be used to score
        score.criteria.noDataFound.scoreType=no_score
        ```
    - Example for zero score 
        ```properties
        #   zero_score : the score value will be 0
        score.criteria.noDataFound.scoreType=zero_score
        ```
    - Example for custom score. We can choose this option to set a custom score percent value
        ```properties
        #   value_percent : specify the value to set in scoreValue param
        score.criteria.noDataFound.scoreType=value_percent
        # When scoreType is value_percent we need to define the value for score
        score.criteria.noDataFound.scoreValue=20
        ```   
        
#### Build Score Calculation Criteria  
 - **Common build widget settings** :
   ```properties
    # Score settings for build widget
    # Number of days to calculate score
    score.buildWidget.numberOfDays=14
    # Weight for the widget out of 100
    score.buildWidget.weight=25
    # If widget is disabled it will not be used for calculating score
    score.buildWidget.disabled=false
    ```
 - **No Widget found, No Data found criterias** :   
   The criteria properties description is same as for default level.
   
   You can add a `propagate` property for  `noWidgetFound` and `noDataFound` criteria. By propagation level, we can replace the score of dashboard/widget by propagating it up. 
   
   **Note**: Propagate value only works for levels up, and there will be no action is you set it as value same or higher than the level where criteria is applied.
    
   The values for `propagate`:
    - no : This is default value and if net there is no propagation
    - widget : the score of criteria will replace the widget score.
    - dashboard : the score of criteria will replace the dashboard score
   ```properties
    # Criteria properties, these will override the default criteria properties
    score.buildWidget.criteria.noWidgetFound.scoreType=zero_score
    score.buildWidget.criteria.noWidgetFound.scoreValue=0
    # Property to propagate score if condition is met
    #   no : do not propagate score (Default)
    #   widget : propagate score as widget score
    #   dashboard : propagate score as dashboard score
    score.buildWidget.criteria.noWidgetFound.propagate=no
    
    # e.g: If no data found set score as 0
    score.buildWidget.criteria.noDataFound.scoreType=zero_score
    score.buildWidget.criteria.noDataFound.scoreValue=0
    score.buildWidget.criteria.noDataFound.propagate=no
    ```    
    
 - **Data Threshold Criteria** :  
   We can define criteria for data thresholds based on percent of builds present in a date range, or days present in a date range
    - Percent Threshold Criteria: This criteria will check whether the build coverage percent meets a criteria and set the score for the criteria. We can set multiple criterias in a array and when a condition is met it will set the score and stop
    
        **Example 1**
        ```properties
        # e.g: If out of 14 days, 4 days have builds the percent of days with builds is 28.6%
        # We want set a threshold that if percent is less or equals 30, set score as 0
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
        score.buildWidget.criteria.dataRangeThresholds[0].value=30
        
        # If the threshold is met set the score
        score.buildWidget.criteria.dataRangeThresholds[0].score.scoreType=zero_score
        score.buildWidget.criteria.dataRangeThresholds[0].score.scoreValue=0
        score.buildWidget.criteria.dataRangeThresholds[0].score.propagate=no
        
        # we can set the last n number of days to check for threshold (Not Mandatory)
        # If this value is not specified it will by default be the `numberOfDays` property of `buildWidget`
        score.buildWidget.criteria.dataRangeThresholds[0].numDaysToCheck=14
        ```
        Calculation
        ```javascript
        var buildDaysPercent = 28.6, 
            score,
            threshold = {
                type : percent,
                comparator : less_or_equal,
                value : 30,
                score : {
                    scoreType : zero_score,
                    scoreValue : 0,
                    propagate : no
                },
                numDaysToCheck : 14
            };
        if (threshold.comparator == less_or_equal && buildDaysPercent <= threshold.value) {
            score = threshold.score
        }
        ```
        This would result in score of widget as 0/100 and would add to the dashboard score by the weight to the widget
       **Example 2**
        ```properties
        # e.g: If out of 14 days, 4 days have builds the percent coverage is 28.6%
        # We want set a threshold that if percent is less or equals 30, set score as 20 and propagate to dashboard
        score.buildWidget.criteria.dataRangeThresholds[0].type=percent
        score.buildWidget.criteria.dataRangeThresholds[0].comparator=less_or_equal
        score.buildWidget.criteria.dataRangeThresholds[0].value=30
        score.buildWidget.criteria.dataRangeThresholds[0].score.scoreType=value_percent
        score.buildWidget.criteria.dataRangeThresholds[0].score.scoreValue=20
        score.buildWidget.criteria.dataRangeThresholds[0].score.propagate=dashboard
        score.buildWidget.criteria.dataRangeThresholds[0].numDaysToCheck=14
        ```
        Calculation
        ```javascript
        var buildDaysPercent = 28.6, 
            score,
            threshold = {
                type : percent,
                comparator : less_or_equal,
                value : 30,
                score : {
                    scoreType : value_percent,
                    scoreValue : 20,
                    propagate : dashboard
                },
                numDaysToCheck : 14
            };
        if (threshold.comparator == less_or_equal && buildDaysPercent <= threshold.value) {
            score = threshold.score
        }
        ```
        This would result in score of widget as 20/100 and propagate it to dashboard would make dashboard score as 20/100 -> 1/5
        
    - Days Threshold Criteria: This criteria will check whether the build coverage days meets a criteria and set the score for the criteria. We can set multiple criterias in a array and when a condition is met it will set the score and stop
    
        **Example 1**
        ```properties
        # e.g: If out of 14 days, 4 days have builds days is 4
        # We want set athreshold that if days coverage is less or equals 5, set score as 0
        # Type of threshold to apply
        #   percent : percent of data
        #   days : number of days where data is present
        score.buildWidget.criteria.dataRangeThresholds[0].type=days
        # Comparator to compare the value
        #   equals,
        #   less,
        #   greater,
        #   less_or_equal,
        #   greater_or_equal
        score.buildWidget.criteria.dataRangeThresholds[0].comparator=less_or_equal
        
        # Value to compare
        score.buildWidget.criteria.dataRangeThresholds[0].value=5
        
        # If the threshold is met set the score
        score.buildWidget.criteria.dataRangeThresholds[0].score.scoreType=zero_score
        score.buildWidget.criteria.dataRangeThresholds[0].score.scoreValue=0
        score.buildWidget.criteria.dataRangeThresholds[0].score.propagate=no
        
        # we can set the last n number of days to check for threshold (Not Mandatory)
        score.buildWidget.criteria.dataRangeThresholds[0].numDaysToCheck=14
        ```
        Calculation
        ```javascript
        var coverageDays = 4,
            score,
            threshold = {
                type : days,
                comparator : less_or_equal,
                value : 5,
                score : {
                    scoreType : zero_score,
                    scoreValue : 0,
                    propagate : no
                },
                numDaysToCheck : 14
            };
        if (threshold.comparator == less_or_equal && coverageDays <= threshold.value) {
            score = threshold.score;
        }
        ```
        This would result in score of widget as 0/100 and would add to the dashboard score by the weight to the widget
       **Example 2**
        ```properties
        # e.g: If out of 14 days, 4 days have builds the percent days is 4
        # We want set a threshold that if days is less or equals 5, set score as 20 and propagate to dashboard
        score.buildWidget.criteria.dataRangeThresholds[0].type=days
        score.buildWidget.criteria.dataRangeThresholds[0].comparator=less_or_equal
        score.buildWidget.criteria.dataRangeThresholds[0].value=5
        score.buildWidget.criteria.dataRangeThresholds[0].score.scoreType=value_percent
        score.buildWidget.criteria.dataRangeThresholds[0].score.scoreValue=20
        score.buildWidget.criteria.dataRangeThresholds[0].score.propagate=dashboard
        score.buildWidget.criteria.dataRangeThresholds[0].numDaysToCheck=14
        ```
        Calculation
        ```javascript
        var coverageDays = 4, 
            score,
            threshold = {
                type : days,
                comparator : less_or_equal,
                value : 5,
                score : {
                    scoreType : value_percent,
                    scoreValue : 20,
                    propagate : dashboard
                },
                numDaysToCheck : 14
            };
        if (threshold.comparator == less_or_equal && coverageDays <= threshold.value) {
            score = threshold.score;
        }
        ```
        This would result in score of widget as 20/100 and propagate it to dashboard would make dashboard score as 20/100 -> 1/5
        
 - **Properties for build duration and status score** :          
    ```properties
    # Build duration within threshold score settings
    score.buildWidget.duration.buildDurationThresholdInMillis=300000
    score.buildWidget.duration.weight=50
    score.buildWidget.duration.disabled=false
    
    # Build duration within threshold score settings
    score.buildWidget.status.weight=50
    score.buildWidget.status.disabled=false
    ```
#### Quality Score Calculation Criteria 
 - **Common quality widget settings** :
   ```properties
    # Score settings for quality widget
    # Weight for the widget out of 100
    score.qualityWidget.weight=25
    # If widget is disabled it will not be used for calculating score
    score.qualityWidget.disabled=false
    ```
- **No Widget found, No Data found criterias** :   
   The criteria properties description is same as for default level.  
   There is a `propagate` property added which can be used to propagate the score to dashboard. If this is set and criteria fails it will be set as dashboard score
   ```properties
    # Criteria properties, these will override the default criteria properties
    score.qualityWidget.criteria.noWidgetFound.scoreType=zero_score
    score.qualityWidget.criteria.noWidgetFound.scoreValue=0
    # Property to propagate score if condition is met
    #   no : do not propagate score
    #   dashboard : propagate score as dashboard score
    score.qualityWidget.criteria.noWidgetFound.propagate=no
    
    # e.g: If no data found set score as 0
    score.qualityWidget.criteria.noDataFound.scoreType=zero_score
    score.qualityWidget.criteria.noDataFound.scoreValue=0
    score.qualityWidget.criteria.noDataFound.propagate=no
    ```      
 - **Code Coverage settings** :    
   The criteria properties for Code Coverage are similar to other criteria settings, except
   - there is no noWidgetFound criteria
   - there is only percent criteria in dataRangeThresholds and the comparison is with the percent of Code Coverage instead
   - The propagate has option to propagate score at widget level or dashboard level
        ```properties
        # Code Quality widget criteria settings
        score.qualityWidget.codeCoverage.weight=50
        score.qualityWidget.codeCoverage.disabled=false
        score.qualityWidget.codeCoverage.criteria.noDataFound.scoreType=zero_score
        score.qualityWidget.codeCoverage.criteria.noDataFound.scoreValue=0
        score.qualityWidget.codeCoverage.criteria.noDataFound.propagate=widget
        # e.g: If data is less than equal to 20% for Code Coverage set score as 0
        score.qualityWidget.codeCoverage.criteria.dataRangeThresholds[0].type=percent
        score.qualityWidget.codeCoverage.criteria.dataRangeThresholds[0].comparator=less
        score.qualityWidget.codeCoverage.criteria.dataRangeThresholds[0].value=20
        score.qualityWidget.codeCoverage.criteria.dataRangeThresholds[0].score.scoreType=zero_score
        score.qualityWidget.codeCoverage.criteria.dataRangeThresholds[0].score.scoreValue=0
        score.qualityWidget.codeCoverage.criteria.dataRangeThresholds[0].score.propagate=widget
        ```

 - **Unit Tests settings** :    
   The criteria properties for Unit Tests are similar to other criteria settings, except
   - there is no noWidgetFound criteria
   - there is only percent criteria in dataRangeThresholds and the comparison is with the percent of Unit Tests pass instead
   - The propagate has option to propagate score at widget level or dashboard level
        ```properties
        # Unit Tests widget criteria settings
        score.qualityWidget.unitTests.weight=50
        score.qualityWidget.unitTests.disabled=false
        score.qualityWidget.unitTests.criteria.noDataFound.scoreType=zero_score
        score.qualityWidget.unitTests.criteria.noDataFound.scoreValue=0
        score.qualityWidget.unitTests.criteria.noDataFound.propagate=widget
        # e.g: If data is less than equal to 100% for Unit Tests set score as 0
        score.qualityWidget.unitTests.criteria.dataRangeThresholds[0].type=percent
        score.qualityWidget.unitTests.criteria.dataRangeThresholds[0].comparator=less
        score.qualityWidget.unitTests.criteria.dataRangeThresholds[0].value=100
        score.qualityWidget.unitTests.criteria.dataRangeThresholds[0].score.scoreType=zero_score
        score.qualityWidget.unitTests.criteria.dataRangeThresholds[0].score.scoreValue=0
        score.qualityWidget.unitTests.criteria.dataRangeThresholds[0].score.propagate=widget
        ```
#### Deploy Score Calculation Criteria 
Deploy properties are similar to quality widget, and data range thresholds can be set only for deploys per day widget
```properties
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
```

#### Github SCM Score Calculation Criteria 
Criteria can be applied to both scmWidget and its sub-widgets `Commits Per Day` and `Commits With PR`
```properties

# Score settings for GitHub SCM widget
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
```

## Examples of Application Properties Files

### Default Settings

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

### Enable Three Widgets (Github, Build, Quality)

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

### Detailed Settings for All Widgets

Detailed settings for all widgets and its categories: GitHub, Build, Quality, Deploy

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