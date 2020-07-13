// Karma configuration
// Generated on Mon Apr 11 2016 15:57:58 GMT-0400 (EDT)

module.exports = function(config) {
    config.set({

        // base path that will be used to resolve all patterns (eg. files, exclude)
        basePath: '',


        // frameworks to use
        // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
        frameworks: ['jasmine'],


        // list of files / patterns to load in the browser
        files: [
            'dist/node_modules/angular.js',
            'dist/node_modules/angular-animate.min.js',
            'dist/node_modules/Chart.min.js',
            'dist/node_modules/angular-chart.js',
            'node_modules/angular-mocks/angular-mocks.js',
            'node_modules/angular-route/angular-route.js',
            'dist/node_modules/angular-sanitize.min.js',
            'node_modules/angular-ui-bootstrap/dist/ui-bootstrap.js',
            'dist/node_modules/ui-bootstrap-tpls.js',
            'dist/node_modules/angular-ui-router.js',
            'dist/app/dashboard/core/extensions/ng-fitText.js',
            'dist/node_modules/angular-chartist.js',
            'dist/node_modules/angular-cookies.min.js',
            'dist/node_modules/angular-validation-match.min.js',
            'dist/node_modules/ng-sortable.min.js',
            'dist/etc/gridstack-angular.js',
            'dist/node_modules/select.min.js',
            'dist/node_modules/angular-jwt.min.js',
            'dist/node_modules/dirPagination.js',
            'dist/node_modules/ng-rateit.min.js',
            'test/appGlobals.js',
            'src/app/app.js',
            'src/app/dashboard/core/module.js',
            'src/components/templates/capone.js',
            'src/app/dashboard/core/data-factories/cloud-data.js',
            'src/components/widgets/cloud/view.js',
            'src/components/widgets/cloud/config.js',
            // files for authInterceptor testing
            'dist/app/dashboard/services/authInterceptor.js',
            'dist/app/dashboard/services/tokenService.js',
            'dist/app/dashboard/services/loginRedirectService.js',
            'dist/app/dashboard/core/data-factories/signup-data.js',
            'dist/app/dashboard/core/data-factories/login-data.js',
            'dist/app/dashboard/services/authService.js',
            'dist/app/dashboard/services/userService.js',
            // end of authInterceptor testing
            'test/**/*.test.js'
        ],


        // list of files to exclude
        exclude: [
        ],


        // preprocess matching files before serving them to the browser
        // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
        preprocessors: {
        },


        // test results reporter to use
        // possible values: 'dots', 'progress'
        // available reporters: https://npmjs.org/browse/keyword/karma-reporter
        reporters: ['progress'],


        // web server port
        port: 9876,


        // enable / disable colors in the output (reporters and logs)
        colors: true,


        // level of logging
        // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
        logLevel: config.LOG_INFO,


        // enable / disable watching file and executing tests whenever any file changes
        autoWatch: true,


        // start these browsers
        // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
        browsers: ['Chrome'],


        // Continuous Integration mode
        // if true, Karma captures browsers, runs the tests and exits
        singleRun: false,

        // Concurrency level
        // how many browser should be started simultaneous
        concurrency: Infinity
    })
}
