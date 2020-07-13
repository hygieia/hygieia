# Centralized cert directory
Store your entreprise crt in this directory to make them available to any collector.

Procedure:
1. Add your crt in this directory
2. Add a volume in your docker-compose.override.yml to mount the directory:
```
volumes:
  - ./certs:/certs
```
3. Depending on the collector's environment, create a env variable named CACERTS that points to the local cacert
4. Reuse the script found in collectors/scm/bitbucket/docker/properties-builder.sh
