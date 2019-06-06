# Karate
## Web-Services Testing Made `Simple.`
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.intuit.karate/karate-core/badge.svg)](https://mvnrepository.com/artifact/com.intuit.karate/karate-core) [![Build Status](https://travis-ci.org/intuit/karate.svg?branch=master)](https://travis-ci.org/intuit/karate) [![GitHub release](https://img.shields.io/github/release/intuit/karate.svg)](https://github.com/intuit/karate/releases) [![Support Slack](https://img.shields.io/badge/support-slack-red.svg)](https://github.com/intuit/karate/wiki/Support) [![Twitter Follow](https://img.shields.io/twitter/follow/KarateDSL.svg?style=social&label=Follow)](https://twitter.com/KarateDSL)

[Karate](https://github.com/intuit/karate) enables you to script a sequence of calls to any kind of web-service and assert that the responses are as expected.  It makes it really easy to build complex request payloads, traverse data within the responses, and chain data from responses into the next request. Karate's payload validation engine can perform a 'smart compare' of two JSON or XML documents without being affected by white-space or the order in which data-elements actually appear, and you can opt to ignore fields that you choose.

Since Karate is built on top of [Cucumber-JVM](https://github.com/cucumber/cucumber-jvm), you can run tests and generate reports like any standard Java project. But instead of Java - you write tests in a language designed to make dealing with HTTP, JSON or XML - **simple**.

## Hello World

<a href="https://gist.github.com/ptrthomas/d5a2d9e15d0b07e4f1b46f692a599f93"><img src="api-audit/src/test/resources/karate-hello-world.jpg" height="400" /></a>


It is worth pointing out that JSON is a 'first class citizen' of the syntax such that you can express payload and expected data without having to use double-quotes and without having to enclose JSON field names in quotes.  There is no need to 'escape' characters like you would have had to in Java or other programming languages.

And you don't need to create Java objects (or POJO-s) for any of the payloads that you need to work with.

## Setup and Run Tests

Make sure a local instance of [api-audit service](http://capitalone.github.io/Hygieia/api-audit.html) is running on your local host. 

If you running tests for api-audit

```$> cd Hygieia/api-tests/api-audit```

```$> mvn clean install```

If you want to add tests to api, create a sub project `api` in api-tests project and create similar folder structure as `api-tests/api-audit` 