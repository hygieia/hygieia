#!/bin/bash

cp travis-utilities/.travis.settings.xml $HOME/.m2/settings.xml

gpg --fast-import keys.gpg

shred keys.gpg

mvn deploy -q -P release -pl 'core'
mvn deploy -q -P release -pl 'api'
mvn deploy -q -P release -pl 'api-audit'
mvn deploy -q -P release -pl 'collectors/feature/rally'
mvn deploy -q -P release -pl 'collectors/artifact/artifactory'
mvn deploy -q -P release -pl 'collectors/build/bamboo'
mvn deploy -q -P release -pl 'collectors/build/jenkins'
mvn deploy -q -P release -pl 'collectors/build/jenkins-cucumber'
mvn deploy -q -P release -pl 'collectors/build/jenkins-codequality'
mvn deploy -q -P release -pl 'collectors/build/sonar'
mvn deploy -q -P release -pl 'collectors/cloud/aws'
mvn deploy -q -P release -pl 'collectors/deploy/udeploy'
mvn deploy -q -P release -pl 'collectors/deploy/xldeploy'
mvn deploy -q -P release -pl 'collectors/feature/jira'
mvn deploy -q -P release -pl 'collectors/feature/versionone'
mvn deploy -q -P release -pl 'collectors/feature/gitlab'
mvn deploy -q -P release -pl 'collectors/misc/chat-ops'
mvn deploy -q -P release -pl 'collectors/performance/appdynamics'
mvn deploy -q -P release -pl 'collectors/scm/bitbucket'
mvn deploy -q -P release -pl 'collectors/scm/github'
mvn deploy -q -P release -pl 'collectors/scm/github-graphql'
mvn deploy -q -P release -pl 'collectors/scm/subversion'
mvn deploy -q -P release -pl 'collectors/scm/gitlab'
mvn deploy -q -P release -pl 'collectors/cmdb/hpsm'
mvn deploy -q -P release -pl 'collectors/library-policy/nexus-iq-collector'
mvn deploy -q -P release -pl 'collectors/misc/score'
mvn deploy -q -P release -pl 'hygieia-jenkins-plugin'
mvn deploy -q -P release -pl 'UI'