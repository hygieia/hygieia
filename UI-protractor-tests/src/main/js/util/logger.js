/**
 * Created by emb235 on 9/19/17.
 */

'use strict';

var logger = new Logger();

var q = require('q');
var loglevelLog = require('loglevel');
var screenshotreporter = require("../reporters/screenshot-reporter");

// todo: -Dlog.leve=trace
loglevelLog.setLevel(loglevelLog.levels.TRACE)

var stackTrace = require('stack-trace');
var flow = protractor.promise.controlFlow();

function Logger() {

    var testLog = [];
    var index = 0;

    this.testLog = function() {
        return testLog;
    }

    this.resetTestLog = function() {
        testLog = [];
    }

    this.step = function(message) {

        flow.execute(function() {
            ++index;
            logger.info("Step " + index + ": " + message);
            screenshotreporter.publishLogAndScreenshot(message, index, testLog).then(
                function(){
                    // resetting testLog
                    testLog = [];
                }
            );
        });

    }

    this.resetStepIndex = function() {
        index = 0;
    }

    this.trace = function(message, showStack) {

        log("trace",message,false);
    }

    this.debug = function(message, showStack) {

        log("debug",message,showStack);
    }

    this.info = function(message, showStack) {

        log("info",message,showStack);
    }

    this.warn = function(message, showStack) {

        log("warn",message,showStack);
    }

    this.error = function(message, showStack) {

        log("error",message,showStack);
    }

    function log(level,message, showStack) {

        var trace = stackTrace.get();

        // start after this object and this inner function, i.e. at 2
        var splits = trace[2].getFileName().split("/");
        var cat = splits[splits.length-1];
        var line =  trace[2].getLineNumber();

        var time = new Date();
        var currentTime = ("0" + time.getHours()).slice(-2) + ":" + ("0" + time.getMinutes()).slice(-2) + ":" + ("0" + time.getSeconds()).slice(-2);

        var logMsg = "["+currentTime+" "+level.toUpperCase()+"("+cat+":"+line+")] "+message;
        loglevelLog[level](logMsg);
        testLog.push(logMsg);

        var stack="";

        if (showStack) {

            loglevelLog[level]("\n   Stacktrace: \n");
            for (var i = 2, len = trace.length; i < len; i++) {
                stack +="   at " +trace[i] +"\n";
            }

            loglevelLog[level](stack);
        }
    }
}

module.exports = logger;


