#!/bin/bash

# if we are linked, use that info
# docker-compose uses depends_on, but while building the following will fail since it
# assumes a hard-dependency on 'mongodb'
# if [ "$MONGO_STARTED" != "" ]; then
  # Sample: MONGO_PORT=tcp://172.17.0.20:27017
  mongo db/admin /tmp/db-setup.js
# fi