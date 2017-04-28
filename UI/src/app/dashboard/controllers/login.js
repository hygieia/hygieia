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
        $scope.isStandardLogin = true;
        login.templateUrl = 'app/dashboard/views/navheader.html';
        login.invalidUsernamePassword = false;

        $scope.showStandard = function () {
          $scope.isStandardLogin = true;
        }

        $scope.showLdap = function () {
          $scope.isStandardLogin = false;
        }

        var signup = function () {
            $location.path('/signup');
        };

        $scope.standardLogin = { name: 'Standard Login', login: authService.login, signup: signup };
        $scope.ldapLogin = { name: 'Ldap Login', login: authService.loginLdap };

    }
    app.controller('LoginController', inject.concat([LoginController]));
})();
