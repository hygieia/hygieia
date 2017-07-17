[![Docker Stars](https://img.shields.io/docker/stars/capitalone/hygieia-ui.svg)](https://hub.docker.com/r/capitalone/hygieia-api/)
[![Docker Stars](https://img.shields.io/docker/pulls/capitalone/hygieia-ui.svg)](https://hub.docker.com/r/capitalone/hygieia-api/)

## Hygieia℠ UI

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

Local Testing with Mocks:

```bash
 gulp serve --local true
```

Using browser-sync's [`ghostMode`](https://www.browsersync.io/docs/options#option-ghostMode) functionality:
```bash
gulp serve:ghost-mode
```

or you can run via maven from UI project root folder
 ```bash
 mvn clean package integration-test
 ```
 for local testing of Hygieia UI layer

All data is currently coming from the test-data folder so you shouldn't need an api, but also means no settings will be saved..


### Docker

#### Create

```bash
# from top-level project
mvn clean package -pl UI docker:build
```

#### Run

```bash
docker run -t -p 8088:80 --link hygieia-api -i hygieia-ui:latest
```
### API server running on a custom port
If the API server is running on a port other than the default (`8080`) then modify `UI/gulpfile.js` to include the custom port:
```
// Using port 8888 for the API server instead of the default (8080)
var proxyTarget = config.api || 'http://localhost:8888';
```

### API check

#### API layer successfully connected
![Image](/media/images/apiup.png)

#### API layer connection unsuccessful
![Image](/media/images/apidown.png)


### ScreenShot of login page with API Layer up
![Image](/media/images/loginpage.png)

### Encryption for private repos
1. From module core generate a secret key.
```
java -jar <path-to-jar>/core-2.0.5-SNAPSHOT.jar com.capitalone.dashboard.util.Encryption
```
2. Add this generated key to api.properties
### api.properties
```
key=<your-generated-key>
```
3. Add the same key to your repo settings file.
This is needed for the target collector to decrypt your saved repo password.
For example, if your repo is github add the following.
### github.properties
```
github.key=<your-generated-key>
```
