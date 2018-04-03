FROM docker.io/nginx:latest

COPY docker/default.conf /etc/nginx/conf.d/default.conf.templ
COPY docker/conf-builder.sh /usr/bin/conf-builder.sh
COPY dist /usr/share/nginx/html
RUN chown -R nginx:nginx /usr/share/nginx/html/

EXPOSE 80 443

CMD conf-builder.sh &&\
  nginx -g "daemon off;"
