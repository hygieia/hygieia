tools utility
====================

This is to provide some basic tool functionality
Encryption key generation
Encryption for passwords

### Building

mvn clean package

Should generate output in the target directory

### Usage
```bash
# Generate an encryption key
java -jar tools-xxx.jar -genkey

# Encrypt a password based on a encryption key
java -jar tools-xxx.jar -encrypt <key> <password>
```

In the case of the GitHubCollector
update the application.properties with a github.key from the genkey output

To encrypt a user password in mongo
1. Add a repository through the UI Code Reposity Widget (select github)
2. Create a github account with access to your repository, always use a system account never a personal account
3. Generate a key and add it to the GitHubCollector application.property
4. Generate the encrypted password using the github password
5. Update mongo for that repository

```mongo
db.collector_items.update(
			  {"options.url":"https://github.com/<company>/<repo>"},
			  {$set: {"options.userID": "<account>", "options.password": "<encrypted password>"}}
			 )

```
