## Hygieia UI

### Requirements

- NodeJS
- npm
- gulp
- bower

#### Mac OS X

    ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
    brew install node
    npm install -g bower
    npm install -g gulp

Pull down everything that's configured with bower and npm. I think it's:

    npm install
    bower install

Will need up update the ngFitText bower.json file to point to 'src/ng-FitText.js' instead of '/src/ng-FitText.js'

#### Windows

Install NodeJS using the MSI package available at: http://nodejs.org/download/

Issue the following commands via command line:

	npm install -g bower
	npm install -g gulp

Navigate to your project root via command line and use the following command:

	npm install

Use Git Shell to install bower in the following manner; do so from your project's root directory:

	bower install
	select option 2 when prompted for user input

Run the dashboard from the following command:

	gulp serve



### Layouts
Are under src/components/templates. Currently only capone is used. Just add ```<widget name="[your new widget name]"></widget>``` and you're good to go.
All widgets have to be hardcoded into the layout right now.


### Running
In terminal navigate to the project root and run ```gulp serve```. Should serve up on port 3000.  

or you can run via maven from UI project root folder
 ```bash
 mvn clean package integration-test
 ```
 for local testing of Hygieia UI layer

All data is currently coming from the test-data folder so you shouldn't need an api, but also means no settings will be saved..

By default this expects the api project to run from the same server.
The best way to accomplish this is by using a proxy pass in nginx or apache to take all /api requests and pass them back to the API server.

### To Docker-ize
```bash
mvn clean package docker:build
```

Expectation is you have already dockerized the API and have a mongo configured
```bash
cd Hygieia/api
mvn clean package docker:build

cd ../UI
mvn clean package docker:build

docker run -h mongo -d --name mongo mongo:latest
docker run -h hygieia_api --link mongo:mongo -P -d --name hygieia_api hygieia_api
docker run -d -P -h hygieia_ui --name hygieia_ui --link hygieia_api:api hygieia_ui

# to find the port that hygieia_ui is now running on
docker port hygieia_ui 80

```

Using the ip address of your docker instance either boot2docker ip or docker-machine env <default> and the above port
you should be able to get to the front UI page and create an account.

