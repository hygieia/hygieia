#!/bin/bash

sed s:API_HOST:${API_HOST:-hygieia-api}: /etc/nginx/conf.d/default.conf.templ |\
  sed s:API_PORT:${API_PORT:-8080}: > /etc/nginx/conf.d/default.conf
