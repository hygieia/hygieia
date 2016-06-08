FROM jenkins:latest

#based on MAINTAINER Maxfield Stewart - github: maxfields2000/dockerjenkins_tutorial

#install maven - based on carlossg/docker-maven
USER root
ENV MAVEN_VERSION 3.3.9
RUN curl -kfsSL https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
  && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn
ENV MAVEN_HOME /usr/share/maven

#get sudo so we can chmod the files we copied in the entry point script
#RUN apt-get update && apt-get -y install sudo && useradd -m docker && echo "docker:docker" | chpasswd && adduser jenkins sudo
#RUN apt-get update && apt-get -y install sudo && adduser jenkins sudo


#Create a log for jenkins (best practice) per Maxfield Stewart
#RUN mkdir /var/log/jenkins
#RUN chown -R jenkins:jenkins /var/log/jenkins
#RUN mkdir /var/cache/jenkins
#RUN chown -R jenkins:jenkins /var/cache/jenkins
#ENV JAVA_OPTS -Xmx8192m
#ENV JENKINS_OPTS --handlerCountStartup=100 --handlerCountMax=300 --logfile=/var/log/jenkins/jenkins.log  --webroot=/var/cache/jenkins/war


#setup plugins
COPY plugins.txt /usr/share/jenkins/ref/
RUN /usr/local/bin/plugins.sh /usr/share/jenkins/ref/plugins.txt

#copy the jobs 
ADD jobs /var/jenkins_home/jobs

#override the entry point to use our script
ADD jenkins_entrypoint.sh /usr/local/bin/jenkins_entrypoint.sh
RUN chmod +x /usr/local/bin/jenkins_entrypoint.sh

#We found that installing gosu was messing up the path so we save and restore it
ENV SAVEPATH $PATH

# this is a best practice, to use gosu instead of sudo, see postgres image
# grab gosu for easy step-down from root
RUN gpg --keyserver ha.pool.sks-keyservers.net --recv-keys B42F6819007F00F88E364FD4036A9C25BF357DD4
RUN apt-get update && apt-get install -y --no-install-recommends ca-certificates wget && rm -rf /var/lib/apt/lists/* \
	&& wget -O /usr/local/bin/gosu "https://github.com/tianon/gosu/releases/download/1.2/gosu-$(dpkg --print-architecture)" \
	&& wget -O /usr/local/bin/gosu.asc "https://github.com/tianon/gosu/releases/download/1.2/gosu-$(dpkg --print-architecture).asc" \
	&& gpg --verify /usr/local/bin/gosu.asc \
	&& rm /usr/local/bin/gosu.asc \
	&& chmod +x /usr/local/bin/gosu 


ENV PATH $PATH:$SAVEPATH
#USER jenkins
#ENTRYPOINT ["/bin/tini", "--", "/usr/local/bin/jenkins.sh"]
ENTRYPOINT ["/bin/tini", "--", "/usr/local/bin/jenkins_entrypoint.sh"]




