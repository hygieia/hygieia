#!/bin/bash
set -e

sudo apt-get update
sudo apt-get install apt-transport-https ca-certificates
sudo apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D
sudo sh -c 'echo "deb https://apt.dockerproject.org/repo ubuntu-xenial main" > /etc/apt/sources.list.d/docker.list'
sudo apt-get update
sudo apt-get purge lxc-docker
sudo apt-cache policy docker-engine
sudo apt-get install -y linux-image-extra-$(uname -r)
sudo apt-get install -y docker-engine git openjdk-8-jre openjdk-8-jdk-headless maven
sudo service docker start
sudo systemctl enable docker
cat /etc/group | grep docker &> /dev/null || sudo groupadd docker
sudo usermod -aG docker $(whoami)

sudo curl -L https://github.com/docker/compose/releases/download/1.19.0/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
docker-compose --version

ls | grep Hygieia &> /dev/null || git clone https://github.com/aimtheory/Hygieia.git
cd Hygieia
git checkout docker_updates
git pull
mvn install
# For Vagrant use, copies file from Vagrant root
if [ ! -f docker-compose.override.yml ]; then
  cp ../docker-compose.override.yml .
fi
docker-compose up -d
