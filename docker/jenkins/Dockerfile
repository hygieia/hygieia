FROM jenkins:1.642.1

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
RUN apt-get update && apt-get -y install sudo && adduser jenkins sudo

#Create a log for jenkins (best practice) per Maxfield Stewart
RUN mkdir /var/log/jenkins
RUN chown -R jenkins:jenkins /var/log/jenkins
RUN mkdir /var/cache/jenkins
RUN chown -R jenkins:jenkins /var/cache/jenkins
ENV JAVA_OPTS -Xmx8192m
ENV JENKINS_OPTS --handlerCountStartup=100 --handlerCountMax=300 --logfile=/var/log/jenkins/jenkins.log  --webroot=/var/cache/jenkins/war


#setup plugins
COPY plugins.txt /usr/share/jenkins/ref/
RUN /usr/local/bin/plugins.sh /usr/share/jenkins/ref/plugins.txt

#copy the jobs 
ADD jobs /var/jenkins_home/jobs

#override the entry point to use our script
ADD jenkins_entrypoint.sh /usr/local/bin/jenkins_entrypoint.sh
RUN chmod +x /usr/local/bin/jenkins_entrypoint.sh

USER jenkins

#ENTRYPOINT ["/bin/tini", "--", "/usr/local/bin/jenkins.sh"]
ENTRYPOINT ["/bin/tini", "--", "/usr/local/bin/jenkins_entrypoint.sh"]




