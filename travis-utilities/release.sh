#!/bin/bash

cp travis-utilities/.travis.settings.xml $HOME/.m2/settings.xml

openssl aes-256-cbc -K $encrypted_229149f15c3f_key -iv $encrypted_229149f15c3f_iv -in travis-utilities/keys.gpg.enc -out keys.gpg -d

gpg --fast-import keys.gpg

shred keys.gpg

mvn deploy -q -P release -pl '!hygieia-jenkins-plugin'
