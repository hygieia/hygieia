// const
//     glob         = require('glob'),
//     protractor   = require.resolve('protractor'),
//     node_modules = protractor.substring(0, protractor.lastIndexOf('node_modules') + 'node_modules'.length),
//     seleniumJar  = glob.sync(`${node_modules}/protractor/**/selenium-server-standalone-*.jar`).pop();

var rootCas = require('ssl-root-cas/latest').create();
require('https').globalAgent.options.ca = rootCas;

exports.config = {

    baseUrl: 'https://hygieia-qa.cloud.capitalone.com/',

    // seleniumServerJar: seleniumJar,

    // https://github.com/angular/protractor/blob/master/docs/timeouts.md
    allScriptsTimeout: 110000,

    disableChecks: true,

    directConnect: true,

    // https://github.com/protractor-cucumber-framework/protractor-cucumber-framework#uncaught-exceptions
    ignoreUncaughtExceptions: true,

    framework: 'custom',
    // frameworkPath: require.resolve('protractor-cucumber-framework'),
    frameworkPath: require.resolve('serenity-js'),

    specs: [ 'src/main/js/features/**/*.feature' ],

    cucumberOpts: {
        require:    [ 'src/main/js/step_definitions/*.steps.js' ],
        format:     'pretty'
        // compiler:   'ts:ts-node/register'
    },

    capabilities: {
        browserName: 'chrome',
        chromeOptions: {
            args: [
                'disable-infobars'
                // 'incognito',
                // 'disable-extensions',
                // 'show-fps-counter=true'
            ]
        }
    }
};
