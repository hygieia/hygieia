#!/bin/bash

cp travis-utilities/.travis.settings.xml $HOME/.m2/settings.xml

mvn deploy -q
