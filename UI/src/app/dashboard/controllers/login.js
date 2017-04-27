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
        
        
        $http({
        	  method: 'GET',
        	  url: '/api/authType'
        	}).then(function successCallback(response) {
        		if(response.data.includes("LDAP")){
        			$scope.showLDAPTab = true;
        			$scope.isStandardLogin = false;
        		}
        		if(response.data.includes("STANDARD")){
        			$scope.showStandardTab = true;
        			$scope.isStandardLogin = true;
        		} 
        	  }, function errorCallback(response) {
        	    
        	  });
    }
    app.controller('LoginController', inject.concat([LoginController]));
})();
