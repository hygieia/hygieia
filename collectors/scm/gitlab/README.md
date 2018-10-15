The README is in the [gh-pages](https://github.com/capitalone/Hygieia/blob/gh-pages/pages/hygieia/collectors/scm/gitlab.md) branch. Please update it there.


# Topol, I don't want to add the complication of another pull request
# to add this data to the new README file location since it is only
# a few lines anyway.  For this reason, I have provided the data
# directly from the 2.0.4 file with context:


#If your instance of Gitlab is using a self signed certificate, set to true, default is false
gitlab.selfSignedCertificate=false

#Gitlab API Token (required, token of user the collector will use by default, can be overriden on a per repo basis from the UI. API token provided by Gitlab)
gitlab.apiToken=

===================== BEGIN NEW LINES ADDED ===========================
# Set timeout (in milliseconds) for socket connect/read and retry count
gitlab.socketConnectTimeoutMS=10000
gitlab.socketReadtimeoutMS=5000
gitlab.socketRetries=5

===================== END NEW LINES ADDED =============================
#Maximum number of days to go back in time when fetching commits
gitlab.commitThresholdDays=15
