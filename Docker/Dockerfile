#
#  Dockerfile for Hygieia setup
#  
#  Start and build the container with ./createdocker
#
#
#  start the container manually with
#
# docker run -d --publish 3000:3000 --env TZ="Europe/Amsterdam" --name hygieia --hostname hygieia -v /root/docker/mongodb:/data hygieia
#
# Caveats: the mongo datafiles are a local volume 
#   (/root/rocker/mongodb in the example) that needs to be 
#   changed accordingly. The first time you start Hygieia 
#   the db.dashboarddb database won't exist. 
#   This might cause failures.

FROM ubuntu:14.04

MAINTAINER Peter van Mourik <tyrion70@gmail.com>

# -----------------------------------------------------------------------------
# update and install packages
# -----------------------------------------------------------------------------
RUN apt-get -y update 
RUN apt-get -y install software-properties-common
RUN add-apt-repository ppa:openjdk-r/ppa
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 7F0CEB10
RUN echo "deb http://repo.mongodb.org/apt/ubuntu "$(lsb_release -sc)"/mongodb-org/3.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-3.0.list
RUN apt-get -y update 
RUN apt-get -y upgrade
RUN apt-get -y install openjdk-8-jdk wget git mongodb-org supervisor curl make gcc g++
RUN apt-get clean all

# -----------------------------------------------------------------------------
# setup node and gulp
# -----------------------------------------------------------------------------
RUN curl --silent --location https://deb.nodesource.com/setup_0.12 | bash -
RUN apt-get install --yes nodejs
RUN curl https://www.npmjs.com/install.sh | bash -
RUN npm install npm@latest -g
RUN npm install -g gulp
RUN npm cache clean
#RUN npm install -g node-gyp
#RUN npm install -g grunt-cli
#RUN npm install -g gyp
#RUN npm install -g bower
#RUN npm install -g phantomjs
#RUN npm install -g karma

# -----------------------------------------------------------------------------
# get and install maven 3.2.5
# -----------------------------------------------------------------------------
RUN cd /tmp && curl --silent --show-error -O http://archive.apache.org/dist/maven/maven-3/3.2.5/binaries/apache-maven-3.2.5-bin.tar.gz
# verify checksum
RUN echo "b2d88f02bd3a08a9df1f0b0126ebd8dc /tmp/apache-maven-3.2.5-bin.tar.gz" | md5sum -c
# install 
RUN F=apache-maven-3.2.5-bin.tar.gz && tar xzf /tmp/${F} -C /opt/  && \
 ln -s /opt/apache-maven-3.2.5 /opt/maven && \
 ln -s /opt/maven/bin/mvn /usr/local/bin/mvn && \
 rm /tmp/${F}

ENV MAVEN_HOME /opt/maven

RUN mkdir -p /data/db
RUN chown -R mongodb:mongodb /data/

# -----------------------------------------------------------------------------
# add the hygieia user 
# -----------------------------------------------------------------------------
RUN useradd -ms /bin/bash hygieia
#RUN psword=`< /dev/urandom tr -dc _A-Z-a-z-0-9 | head -c32` && usermod --password $psword hygieia
#RUN chown -R hygieia.hygieia /home/hygieia

# -----------------------------------------------------------------------------
# custom source -> if using local source in .tar.gz form
# -----------------------------------------------------------------------------
#ADD Hygieia.tar.gz /var
#RUN chmod -R 777 /var/Hygieia
# -----------------------------------------------------------------------------
# github -> change github url here
# -----------------------------------------------------------------------------
RUN git clone https://github.com/capitalone/Hygieia.git
RUN mv Hygieia /var

RUN chown hygieia:hygieia /var/Hygieia
RUN chown -R hygieia:hygieia /var/Hygieia
RUN echo "cd /var/Hygieia && mvn install" | su - hygieia

ADD mongodb.conf /etc/supervisor/conf.d/mongodb.conf
ADD ui.conf /etc/supervisor/conf.d/ui.conf

ADD api.conf /etc/supervisor/conf.d/api.conf
ADD api.properties /var/Hygieia/api/application.properties

ADD jenkins.conf /etc/supervisor/conf.d/jenkins.conf
ADD jenkins.properties /var/Hygieia/BuildCollector/target/application.properties

ADD sonar.conf /etc/supervisor/conf.d/sonar.conf
ADD sonar.properties /var/Hygieia/CodeQualityCollector/target/application.properties

ADD cucumber.conf /etc/supervisor/conf.d/cucumber.conf
ADD cucumber.properties /var/Hygieia/JenkinsCucumberTestCollector/target/application.properties

RUN apt-get install -y apache2
RUN mkdir -p /var/lock/apache2 /var/run/apache2 
ADD apachesupervisor.conf /etc/supervisor/conf.d/apache2.conf
ADD 000-default.conf /etc/apache2/sites-available/000-default.conf
ADD apachedirectory.conf /etc/apache2/conf-enabled/apachedirectory.conf
RUN ln -s /var/Hygieia/UI/bower_components /var/Hygieia/UI/src/bower_components
RUN a2enmod proxy
RUN a2enmod proxy_http
CMD /usr/bin/supervisord -n
