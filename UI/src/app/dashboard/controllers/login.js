/**
 * Controller for performing authentication or signingup a new user */
(function () {
    'use strict';
    var app = angular.module(HygieiaConfig.module)
    var inject = ['$http', '$location', '$scope', 'authService', 'userService', 'loginRedirectService']
    function LoginController($http, $location, $scope, authService, userService, loginRedirectService) {
        if (userService.isAuthenticated()) {
            $location.path('/');
            return;
        }
        var login = this;
        login.isStandardLogin = true;
        login.templateUrl = 'app/dashboard/views/navheader.html';
        login.apiup = false;
        login.username = '';
        login.password = '';
        login.invalidUsernamePassword = false;
        login.appVersion='';


        login.doLogin = function () {
            $scope.lg.username.$setValidity('invalidUsernamePassword', true);
            var valid = $scope.lg.$valid;
            if (valid) {
                var auth = {'username': login.username, 'password': login.password};
                authService.login(auth)
                    .then(function (response) {
                        if (response.status == 200) {
                            $location.path(loginRedirectService.getRedirectPath());
                        } else if (response.status == 401) {
                            $scope.lg.username.$setValidity(
                                    'invalidUsernamePassword',
                                    false
                                  );
                        }
                    });
            }
        };

        login.doLoginLdap = function () {
            $scope.lg.username.$setValidity('invalidUsernamePassword', true);
            var valid = $scope.lg.$valid;
            if (valid) {
                var auth = {'username': login.username, 'password': login.password};
                authService.loginLdap(auth)
                    .then(function (response) {
                        if (response.status == 200) {
                            $location.path(loginRedirectService.getRedirectPath());
                        } else if (response.status == 401) {
                            $scope.lg.username.$setValidity(
                                    'invalidUsernamePassword',
                                    false
                                  );
                        }
                    });
            }
        };

        login.doSignup = function () {
            $location.path('/signup');
        };

    }
    app.controller('LoginController', inject.concat([LoginController]));
})();
