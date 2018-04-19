#Centralized cert directory
Store your entreprise crt in this directory to make them available to any collector.

Procedure:
- Add your crt in this directory
- Add a volume in your docker-compose.override.yml to mount the directory:

    #volumes:
    #  - ./certs:/certs
	
- Reuse the script found in collectors/scm/bitbucket/docker/properties-builder.sh