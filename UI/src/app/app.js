
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

    // creat the angular app
    angular.module('devops-dashboard', [
        'ngAnimate',
        'ngSanitize',
        'ngRoute',
        'devops-dashboard.core',
        'ui.bootstrap',
        'fitText',
        'angular-chartist',
        'ngCookies',
        'validation.match'
    ])

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
