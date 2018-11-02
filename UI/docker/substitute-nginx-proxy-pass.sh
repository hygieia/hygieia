#!/bin/bash
 
CONF_DIR=/opt/app-root/etc/nginx.default.d
sed -i "s/API_HOST/${API_HOST:-api}/g" ${CONF_DIR}/api.conf
sed -i "s/API_PORT/${API_PORT:-8080}/g" ${CONF_DIR}/api.conf
