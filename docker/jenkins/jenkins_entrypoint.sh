#!/bin/bash

#we have to do this in an entrypoint becuase the jenkins image has defined $JENKINS_HOME as a volume
#this was the recommend approach on the docker site
sudo chown -R jenkins:jenkins /var/jenkins_home/jobs

#call the original entry point
/bin/tini -- /usr/local/bin/jenkins.sh

