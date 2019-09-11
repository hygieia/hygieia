## Prerequisites

To follow the tutorial, you'll need:

* [Node.js and NPM](https://nodejs.org/en/)
* A web browser, ideally [Chrome](https://www.google.co.uk/chrome/browser/desktop/)

You'll also need access to the [command line interface](https://en.wikipedia.org/wiki/Command-line_interface).

The below code listings, which look like this one below, mean "type `whoami` into the command line":

```
$> whoami
```

## Setup

First, make sure that you have the [required tools](http://serenity-js.org/overview/prerequisites.html) installed.
Next, [clone](https://help.github.com/articles/cloning-a-repository/) this project to your computer:

```
$> git clone https://github.com/capitalone/Hygieia.git
$> cd Hygieia
```

Start a mocked version of the UI

```
$> cd UI
$> npm install && bower install
$> gulp serve --local true
```

In a new terminal
```
$> cd UI-protractor-tests
$> npm install
```

And make sure that you can execute the acceptance tests using
[Protractor](https://github.com/angular/protractor) and
[Cucumber](https://github.com/cucumber/cucumber-js):

```
$> npm test
```

Calling the above command should give you output similar to the one below, notifying you of a pending step:

```
Feature: Add new items to the todo list

  In order to avoid having to remember things that need doing
  As a forgetful person
  I want to be able to record what I need to do in a place where I won't forget about them

  Scenario: Adding an item to a list with other items
    Given that James has a todo list containing Buy some cookies, Walk the dog
    When he adds Buy some cereal to his list
    Then his todo list should contain Buy some cookies, Walk the dog, Buy some cereal

```

To run serenity reports, you can execute the following command
```
$> npm run report
```

Go to the target/site folder and open the index.html in a browser and you can see the detailed reports with test execution status and screenshots
