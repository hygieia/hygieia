
// test to see if local storage is supported functionality
var localStorageSupported = (function () {
    try {
        localStorage.setItem('foo', 'bar');
        localStorage.removeItem('foo');
        return true;
    } catch (exception) {
        return false;
    }
})();

(function () {
    'use strict';

    // set default theme
    var theme = 'dash';

    // get theme from storage
    if(localStorageSupported) {
        var tempTheme = localStorage.getItem('theme');
        if(tempTheme && tempTheme != 'undefined' ) {
            theme = tempTheme;
        }
    }

    // add the theme stylesheet in the header
    var link = document.createElement('link');

    link.setAttribute('id', 'theme');
    link.setAttribute('rel', 'stylesheet');
    link.setAttribute('href', 'styles/' + theme + '.css');

    document.getElementsByTagName('head')[0].appendChild(link);

    // create the angular app
    angular.module(HygieiaConfig.module, [
        'ngAnimate',
        'ngSanitize',
        'ngRoute',
        HygieiaConfig.module + '.core',
        'ui.bootstrap',
        'fitText',
        'angular-chartist',
        'ngCookies',
        'validation.match',
        'as.sortable',
        'ui.select'
    ])
    .config(['$httpProvider',
        // intercepting the http provider allows us to use relative routes
        // in data providers and then redirect them to a remote api if
        // necessary
        function ($httpProvider) {
            $httpProvider.interceptors.push(function () {
                return {
                    request: function (config) {
                        var path = config.url;
                        if(config.url.substr(0, 1) != '/') {
                            path = '/' + config.url;
                        }

                        if(!!HygieiaConfig.api && path.substr(0, 5) == '/api/') {
                            config.url = HygieiaConfig.api + path;
                        }

                        return config;
                    }
                };
            });
        }])
    .config(function ($routeProvider) {
            $routeProvider
                // main dashboard page
                .when('/dashboard/:id', {
                    templateUrl: 'app/dashboard/views/dashboard.html',
                    controller: 'DashboardController',
                    controllerAs: 'ctrl',
                    resolve: {
                        dashboard: function ($route, dashboardData) {
                            return dashboardData.detail($route.current.params.id);
                        }
                    }
                })
                // administrative functionality
                .when('/admin', {
                    templateUrl: 'app/dashboard/views/admin.html',
                    controller: 'AdminController',
                    controllerAs: 'ctrl'
                })
                // dashboard selection/creation
                .when('/site', {
                    templateUrl: 'app/dashboard/views/site.html',
                    controller: 'SiteController',
                    controllerAs: 'ctrl'
                })
                //login

                .when('/',{
                  templateUrl: 'app/dashboard/views/login.html',
                  controller: 'LoginController',
                  controllerAs: 'login'
                })

                .when('/signup',{
                  templateUrl:'app/dashboard/views/signup.html',
                  controller: 'SignupController',
                  controllerAs: 'signup'
                })
                .otherwise({
                    redirectTo: '/'
                });
        });
})();
