#!/bin/bash

cp travis-utilities/.travis.settings.xml $HOME/.m2/settings.xml

openssl aes-256-cbc -K $encrypted_230c5046d1fb_key -iv $encrypted_230c5046d1fb_iv -in travis-utilities/keys.gpg.enc -out keys.gpg -d

gpg --fast-import keys.gpg

shred keys.gpg

sed -i 's|-SNAPSHOT||g' pom.xml

mvn deploy -q -P release
