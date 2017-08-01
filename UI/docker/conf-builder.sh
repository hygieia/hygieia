#!/bin/bash


# if we are linked, use that info
#linked containers now use hostnames
export API_HOST=hygieia-api
export API_PORT=8080

sed s:API_HOST:${API_HOST:-127.0.0.1}: /etc/nginx/conf.d/default.conf.templ |\
  sed s:API_PORT:${API_PORT:-8080}: > /etc/nginx/conf.d/default.conf
