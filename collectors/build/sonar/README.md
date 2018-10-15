The README is in the [gh-pages](https://github.com/capitalone/Hygieia/blob/gh-pages/pages/hygieia/collectors/build/sonar.md) branch. Please update it there.

# Topol, I don't want to add the complication of another pull request
# to add this data to the new README file location since it is only
# a few lines anyway.  For this reason, I have provided the data
# directly from the 2.0.4 file with context:



# Sonar server(s) (required) - Can provide multiple
sonar.servers[0]=http://sonar.company.com

===================== BEGIN NEW LINES ADDED ===========================
# Set timeout (in milliseconds) for socket connect/read and retry count
sonar.socketConnectTimeoutMS=30000
sonar.socketReadtimeoutMS=10000
sonar.socketRetries=5

===================== END NEW LINES ADDED =============================
# Sonar Metrics
sonar.metrics=ncloc,line_coverage,violations,critical_violations,major_violations,blocker_violations,sqale_index,test_success_density,test_failures,test_errors,tests
