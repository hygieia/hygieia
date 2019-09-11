#!/bin/bash

#we have to do this in an entrypoint becuase the jenkins image has defined $JENKINS_HOME as a volume
#this was the recommend approach on the docker site
chown -R jenkins:jenkins /var/jenkins_home/

#call the original entry point
#we use gosu as this is the recommended best practice
exec gosu jenkins /usr/local/bin/jenkins.sh

