ChatOpsCollector
=========================

The ChatOpsCollector works a little differently than other collectors as you only have to run it once for widget to register.
We are working to make a robust admin module which will provide the same functionality to register as well as enable/disable a widget
Stay tuned

I have tested this with Enterprise Hipchat version, It is not tested against public hipchat.


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
 * API token (Only v2 style tokens are supported at this time more info [Link](https://www.hipchat.com/docs/apiv2/auth)
