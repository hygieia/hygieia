
exports.config = {

    baseUrl: 'http://localhost:3000/#/',

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
        browserName: 'chrome'
    }
};
