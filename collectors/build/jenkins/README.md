The README is in the [gh-pages](https://github.com/capitalone/Hygieia/blob/gh-pages/pages/hygieia/collectors/build/jenkins.md) branch. Please update it there.

# Topol, I don't want to add the complication of another pull request
# to add this data to the new README file location since it is only
# a few lines anyway.  For this reason, I have provided the data
# directly from the 2.0.4 file with context:



# Another option: If using same username/password Jenkins auth,
#   set username/apiKey to use HTTP Basic Auth (blank=no auth)
jenkins.usernames[0]=
jenkins.apiKeys[0]=

===================== BEGIN NEW LINES ADDED ===========================
# Set timeout (in milliseconds) for socket connect/read and retry count
jenkins.socketConnectTimeoutMS=30000
jenkins.socketReadtimeoutMS=10000
jenkins.socketRetries=5

===================== END NEW LINES ADDED =============================
# Determines if build console log is collected - defaults to false
jenkins.saveLog=true
