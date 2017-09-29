
exports.config = {

    baseUrl: 'https://hygieia-qa.cloud.capitalone.com/',

    sauceUser:'emb235',

    sauceKey:'1e13701e-f9d2-4482-ba1c-fc592a863721',

    sauceProxy:  'http://proxy.kdc.capitalone.com:8099',

    webDriverProxy: 'http://proxy.kdc.capitalone.com:8099',

    allScriptsTimeout: 110000,

    disableChecks: true,

    directConnect: true,

    ignoreUncaughtExceptions: true,

    framework: 'custom',

    frameworkPath: require.resolve('serenity-js'),

    specs: [ 'src/main/js/features/**/*.feature' ],

    cucumberOpts: {
        require:    [ 'src/main/js/step_definitions/*.steps.js' ],
        format:     'pretty'
    },

    capabilities: {
        browserName: 'chrome',
        chromeOptions: {
            args: [
                'disable-infobars'
            ]
        }
    }
};
