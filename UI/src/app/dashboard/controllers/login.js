/**
 * Controller for performing authentication or signingup a new user */
(function () {
    'use strict';
    var app = angular.module(HygieiaConfig.module)
    var inject = ['$http', '$location', '$scope', 'authService', 'userService']
    function LoginController($http, $location, $scope, authService, userService) {
        if (userService.isAuthenticated()) {
            $location.path('/site');
            return;
        }
        var login = this;
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
                            $location.path('/site');
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

        function getAppVersion(){
            var url = '/api/appinfo';
            $http.get(url).success(function (data, status) {
                console.log("appinfo:"+data);
                login.appVersion=data;
                login.apiup = (status == 200);
            }).error(function(data,status){
                console.log("appInfo:"+data);
                login.appVersion="0.0";
                login.apiup = false;
            });
        }
        getAppVersion();

    }
    app.controller('LoginController', inject.concat([LoginController]));
})();
