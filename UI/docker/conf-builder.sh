#!/bin/bash


# if we are linked, use that info
if [ "$HYGIEIA_API_PORT" != "" ]; then
  # Sample: MONGO_PORT=tcp://172.17.0.20:27017
  export API_HOST=`echo $HYGIEIA_API_PORT|sed 's;.*://\([^:]*\):\(.*\);\1;'`
  export API_PORT=`echo $HYGIEIA_API_PORT|sed 's;.*://\([^:]*\):\(.*\);\2;'`
fi

sed s:API_HOST:${API_HOST:-127.0.0.1}: /etc/nginx/conf.d/default.conf.templ |\
  sed s:API_PORT:${API_PORT:-8080}: > /etc/nginx/conf.d/default.conf
