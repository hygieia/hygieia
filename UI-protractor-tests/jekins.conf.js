
exports.config = {

    // seleniumAddress: 'http://localhost:4444/wd/hub',

    baseUrl: 'https://hygieia-qa.cloud.capitalone.com/',

    allScriptsTimeout: 110000,

    disableChecks: true,

    // directConnect: true,

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
        // firefox_binary: '/Applications/Firefox47.app/Contents/MacOS/firefox',
        // acceptInsecureCerts: true
        chromeOptions: {
            binary: '/opt/runtime/chromedriver-2.33'
            // args: [
            //     '--disable-infobars', "--disable-gpu", "--start-maximized", '--disable-extensions'
            // ],
            // prefs: {
            //     // disable chrome's annoying password manager
            //     'profile.password_manager_enabled': false,
            //     'credentials_enable_service': false,
            //     'password_manager_enabled': false
            // }
        }
    }
};
