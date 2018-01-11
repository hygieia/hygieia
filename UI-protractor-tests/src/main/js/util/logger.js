
var logger = new Logger();

var q = require('q');
var loglevelLog = require('loglevel');

// todo: -Dlog.leve=trace
loglevelLog.setLevel(loglevelLog.levels.TRACE)

var stackTrace = require('stack-trace');

function Logger() {

    var testLog = [];

    this.testLog = function() {
        return testLog;
    };

    this.resetTestLog = function() {
        testLog = [];
    };

    this.trace = function(message, showStack) {

        log("trace",message,false);
    };

    this.debug = function(message, showStack) {

        log("debug",message,showStack);
    };

    this.info = function(message, showStack) {

        log("info",message,showStack);
    };

    this.warn = function(message, showStack) {

        log("warn",message,showStack);
    }

    this.error = function(message, showStack) {

        log("error",message,showStack);
    };

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
    };
};

module.exports = logger;


