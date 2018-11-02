#!/bin/bash
 
sed -i "s/API_HOST/${API_HOST:-api}/g" api.conf
sed -i "s/API_PORT/${API_PORT:-8080}/g" api.conf
