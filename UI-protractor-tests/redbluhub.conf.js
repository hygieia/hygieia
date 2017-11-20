
exports.config = {

    seleniumAddress: 'http://bluxpress-fs.cloud.capitalone.com:4444/wd/hub',

    baseUrl: 'https://hygieia-qa.cloud.capitalone.com/',

    allScriptsTimeout: 110000,

    disableChecks: true,

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
        // version: '62.0.3202.62',
        recordVideo : true,
        name : 'Hygieia SmokeTests',
        ignoreProtectedModeSettings : true
    }
};
