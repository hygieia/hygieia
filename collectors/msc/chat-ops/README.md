ChatOpsCollector
=========================

The ChatOpsCollector works a little differently than other collectors as you only have to run it once for widget to register.
We are working to make a robust admin module which will provide the same functionality to register as well as enable/disable a widget
Without having to write and run collector like this one
Stay tuned

I have tested this with Enterprise Hipchat version, It is not tested against public hipchat, But should work


##Supported Chat Application
 * HipChat
 * Slack.com ( Not implemented yet, looking for community to contribute)
 * Gitter.im (Not implemented yet, looking for community to contribute)


##Screenshot of the widget

This is how it looks like

###Widget

![Image](/media/images/chatops.png)

###ChatOps Configure Screen

![Image](/media/images/chatopsconfig.png)

## ChatOps widget configuration parameter
 * Need to provide hipchat server name.
 * Chat Room Name
 * API token (Only v2 style tokens are supported at this time  for more info click the [Link](https://www.hipchat.com/docs/apiv2/auth))
 
## ChatOps Collector properties file
```
  dbname=Your Mongodb Name
  dbusername=your mongodb username
  dbpassword=your mongodb password
  dbhost=your mongodb host
  dbport=your mongodb port
  logging.file=./logs/chatops.log
  chatops.cron=5 * * * * *
 
 ```
Make sure the configuration file chatops.properties is in the same folder where the collector jar is before starting.

## How to start the collector
```
java -jar <chatopscollector.jar> --spring.config.name=chatops

```

if your properties file is in different location than your start command would look like

```
java -jar <chatopscollector.jar> --spring.config.name=chatops --spring.config.location=path to external properties file
```