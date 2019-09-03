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
      'node_modules/angular/angular.js',
      'node_modules/angular-animate/angular-animate.js',
      'node_modules/angular-mocks/angular-mocks.js',
      'node_modules/angular-route/angular-route.js',
      'node_modules/angular-sanitize/angular-sanitize.js',
      'node_modules/angular-bootstrap/ui-bootstrap.js',
      'node_modules/angular-bootstrap/ui-bootstrap-tpls.js',
      'node_modules/angular-ui-router/release/angular-ui-router.js',
      'node_modules/angular-chartist.js/dist/angular-chartist.js',
      'node_modules/angular-cookies/angular-cookies.js',
      'node_modules/angular-validation-match/src/angular-validation-match.js',
      'node_modules/angular-ui-select/dist/select.js',
      'node_modules/angular-jwt/dist/angular-jwt.js',
      'node_modules/angular-utils-pagination/dirPagination.js',
      'node_modules/angular-rateit/dist/ng-rateit.js',
      'node_modules/ng-sortable/dist/ng-sortable.js',
      'node_modules/chart.js/dist/Chart.js',
      'node_modules/chartist/dist/chartist.js',
      'node_modules/angular-chart.js/dist/angular-chart.js',
      'node_modules/moment/moment.js',
      'node_modules/jquery/dist/jquery.js',
      'node_modules/jquery-ui/jquery-ui.js',
      'node_modules/lodash/lodash.js',
      'node_modules/gridstack-angular/dist/gridstack-angular.js',
      'src/app/dashboard/core/extensions/ng-fitText.js',
      'test/appGlobals.js',
      'src/app/app.js',
      'src/app/dashboard/core/module.js',
      'src/app/dashboard/core/providers/*.js',
      'src/app/dashboard/directives/*.js',
      'src/app/dashboard/services/*.js',
      'src/app/dashboard/controllers/editDashboard.js',
      'src/app/dashboard/core/data-factories/*.js',
      'src/app/dashboard/core/providers/*.js',
      'src/components/templates/capone.js',
      'src/app/dashboard/core/data-factories/cloud-data.js',
      'src/components/widgets/cloud/view.js',
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
    autoWatch: false,


    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    browsers: ['ChromeHeadless'],


    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: true,

    // Concurrency level
    // how many browser should be started simultaneous
    concurrency: Infinity

  })
}
