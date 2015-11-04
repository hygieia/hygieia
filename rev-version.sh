#!/bin/bash

ver=""
if [ "$1" != "" ]; then
  ver="-DdevelopmentVersion=$1"
fi


mvn --batch-mode release:update-versions -DautoVersionSubmodules=true $ver

# release: mvn --batch-mode release:prepare -DautoVersionSubmodules=true 
