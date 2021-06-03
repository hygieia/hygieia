# build environment
FROM node:10.16.0 as builder

# set working directory
RUN mkdir /usr/src/app
WORKDIR /usr/src/app

ENV PATH /usr/src/app/node_modules/.bin:$PATH

COPY package.json /usr/src/app/package.json
RUN npm install
RUN npm install -g @angular/cli@8.0.6 --unsafe

COPY . /usr/src/app

RUN npm run build --output-path=dist

FROM httpd:2.4-alpine

# copy compiled app to server
COPY --from=builder /usr/src/app/dist/hygieia-ui /usr/local/apache2/htdocs/

COPY ./httpd/.htaccess /usr/local/apache2/htdocs/.htaccess

# copy startup script
COPY ./startup.sh /startup.sh

# make script executable
RUN chmod +x /startup.sh

# expose port 80
EXPOSE 80

ENTRYPOINT ["/startup.sh"]

CMD ["httpd-foreground"]