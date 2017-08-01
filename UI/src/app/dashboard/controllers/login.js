/**
 * Controller for performing authentication or signingup a new user */
(function () {
    'use strict';
    var app = angular.module(HygieiaConfig.module)
    var inject = ['$location', '$scope', 'authService', 'userService', 'loginRedirectService']
    function LoginController($location, $scope, authService, userService, loginRedirectService) {
        if (userService.isAuthenticated()) {
            $location.path('/');
            return;
        }
        var login = this;
        login.templateUrl = 'app/dashboard/views/navheader.html';
        login.invalidUsernamePassword = false;


        authService.getAuthenticationProviders().then(function(response) {
          $scope.authenticationProviders = response.data;
          $scope.activeTab = response.data[0];
        });

        $scope.isStandardLogin = function () {
          return $scope.activeTab === "STANDARD";
        }

        $scope.isLdapLogin = function () {
          return $scope.activeTab === "LDAP";
        }

        $scope.showStandard = function () {
          $scope.activeTab = "STANDARD";
        }

        $scope.showLdap = function () {
          $scope.activeTab = "LDAP";
        }

        var signup = function () {
            $location.path('/signup');
        };

        $scope.standardLogin = { name: 'Standard Login', login: authService.login, signup: signup };
        $scope.ldapLogin = { name: 'Ldap Login', login: authService.loginLdap };

    }
    app.controller('LoginController', inject.concat([LoginController]));
})();
