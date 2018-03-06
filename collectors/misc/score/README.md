# Hygieia score Collector

This project uses Spring Boot to package the collector as an executable JAR with dependencies.

## Building and Deploying

To package the collector into an executable JAR file, run:
```bash
mvn install
```

Copy this file to your server and launch it using:
```
java -JAR score-collector.jar
```

## application.properties

You will need to provide an **application.properties** file that contains information about how to connect to the Dashboard MongoDB database instance, as well as properties the Score collector requires. See the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files) for information about sourcing this properties file.

### Properties for score calculation

#### DB connection and common properties

```properties
# Database Name
spring.data.mongodb.dbname=dashboard

# Database HostName - default is localhost
spring.data.mongodb.host=10.0.1.1

# Database Port - default is 27017
spring.data.mongodb.port=9999

# Database Username - default is blank
spring.data.mongodb.username=db

# Database Password - default is blank
spring.data.mongodb.password=dbpass

# Collector schedule (required)
score.cron=0 0/5 * * * *

# Max Score
score.maxScore=5
```

#### Common score calculation criteria
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
        
#### Build score calculation criteria  
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
   
   Note: Propagate value only works for levels up, and there will be no action is you set it as value same or higher than the level where criteria is applied.
    
   The values for `propagate`
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
    
 - **Data Threshold Criterias** :  
   We can define criterias for data thresholds based on percent of builds present in a date range, or days present in a date range
    - Percent Threshold Criteria: This criteria will check whether the build coverage percent meets a criteria and set the score for the criteria. We can set multiple criterias in a array and when a condition is met it will set the score and stop
    
        **Example 1**
        ```properties
        # e.g: If out of 14 days, 4 days have builds the percent of days with builds is 28.6%
        # We want set athreshold that if percent is less or equals 30, set score as 0
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
#### Quality score calculation criteria 
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
#### Deploy score calculation criteria 
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

#### Github SCM score calculation criteria 
Criteria can be applied to both scmWidget and its sub-widgets `Commits Per Day` and `Commits With PR`
```properties

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
```
## Examples
- [Default settings](docs/1_default_settings.md)
- [Enable few widgets](docs/2_enable_few_widgets.md)
- [Widget all category settings](docs/3_widget_all_category_settings.md)
