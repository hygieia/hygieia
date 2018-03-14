Vagrant.configure("2") do |config|
  config.vm.box = "ubuntu/xenial64"
  config.vm.provision "file", source: "docker-compose.override.yml", destination: "docker-compose.override.yml"
  config.vm.provision "shell", path: "provision_script.sh"
  config.disksize.size = '12GB'

  config.vm.provider :virtualbox do |vb|
    vb.customize ["modifyvm", :id, "--memory", "4096"]
  end
end
