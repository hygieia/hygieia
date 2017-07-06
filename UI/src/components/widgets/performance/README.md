# Performance Widget UI
###### Version 1.0.0


### Initializing
When you first configure your widget, you will have a dropdown of all the applications configured to your performance tool (currently only AppDynamics). If they are not showing up, there's a high chance that your authentication to the tool in the PROPERTY file is incorrect.

### Collection
Nothing will load until the collector runs. The collector runtime is set to the cron in the PROPERTIES file. Once the collector starts running, the data will appear. The number of data points on the bottom two graphs will gradually increase until there are 7 points. From that point onwards, the graph will only show the seven more recent data. The graph will not display any data times where the metric is 0 (to prevent meaningless data skew).

#### Release Features (Initial Release)
1. Connection with the AppDynamics API
2. Shows data using donut graphs and chart graphs using the ChartistJS

#### Current Bugs & Issues
1. When the collector has run once, the single data point on either of the graphs will be shifted left outside of the graph itself. The problem resolves by itself at the time of the second collection. This is likely a padding issue with Chartistjs, and options can be modified in views.js
2. Collecting time should be modified to be dynamic - find a way you can get the cron information from the PROPERTIES file to the front end so that collecting time is not fixed. Currently it is fixed to last 15 minutes.
3. There's a narrow resolution range in which the Warning pane of the UI collapses so that the numbers are right below the corresponding icons.
