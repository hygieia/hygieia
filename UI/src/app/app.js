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
    if (localStorageSupported) {
        var tempTheme = localStorage.getItem('theme');
        if (tempTheme && tempTheme != 'undefined') {
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
        'ui.router',
        HygieiaConfig.module + '.core',
        'ui.bootstrap',
        'fitText',
        'angular-chartist',
        'chart.js',
        'gridstack-angular',
        'ngCookies',
        'validation.match',
        'as.sortable',
        'ui.select',
        'angular-jwt',
        'angularUtils.directives.dirPagination',
        'ngRateIt'
    ])

        .config(['$httpProvider', 'jwtOptionsProvider',
            // intercepting the http provider allows us to use relative routes
            // in data providers and then redirect them to a remote api if
            // necessary
            function ($httpProvider, jwtOptionsProvider) {
                jwtOptionsProvider.config({
                    tokenGetter: ['tokenService', function (tokenService) {
                        return tokenService.getToken();
                    }]
                });
                $httpProvider.interceptors.push('jwtInterceptor');
                $httpProvider.interceptors.push('authInterceptor');
                $httpProvider.interceptors.push(function () {
                    return {
                        request: function (config) {
                            var path = config.url;
                            if (config.url.substr(0, 1) != '/') {
                                path = '/' + config.url;
                            }

                            if (!!HygieiaConfig.api && path.substr(0, 5) == '/api/') {
                                config.url = HygieiaConfig.api + path;
                            }

                            return config;
                        },
                    };
                });
            }])
        .config(function ($stateProvider, $urlRouterProvider) {

            $urlRouterProvider.otherwise('/');

            $stateProvider
                .state('login', {
                    url: '/login',
                    controller: 'LoginController as login',
                    templateUrl: 'app/dashboard/views/login.html'
                })

                .state('site', {
                    url: '/',
                    controller: 'SiteController as ctrl',
                    templateUrl: 'app/dashboard/views/site.html',
                    resolve: {
                    	user: function (Session) {
                    		return Session.updateSession();
                    	}
                    }
                })

                .state('signup', {
                    url: '/signup',
                    controller: 'SignupController as signup',
                    templateUrl: 'app/dashboard/views/signup.html'
                })

                .state('adminState', {
                    url: '/admin',
                    controller: 'AdminController as ctrl',
                    templateUrl: 'app/dashboard/views/admin.html'
                })

                .state('dashboardState', {
                    url: '/dashboard/:id?delete&reset',
                    controller: 'DashboardController as ctrl',
                    templateUrl: 'app/dashboard/views/dashboard.html',
                    resolve: {
                        dashboard: function ($stateParams, dashboardData) {
                            return dashboardData.detail($stateParams.id);
                        }
                    }
                })

                .state('templates', {
                    url: '/templates',
                    controller: 'TemplateController as ctrl',
                    templateUrl: 'app/dashboard/views/templates.html'
                })

        })
        .run(function ($rootScope, loginRedirectService) {
            $rootScope.$on('$locationChangeStart', function (event, nextPath, currentPath) {
                loginRedirectService.saveCurrentPath(currentPath);
            });
        });
})();
