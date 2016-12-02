/**
 * Controller for performing authentication or signingup a new user */
(function () {
    'use strict';
    var app = angular.module(HygieiaConfig.module)
    var inject = ['$cookies', '$http', '$location', '$scope', 'loginData']
    function LoginController($cookies, $http, $location, $scope, loginData) {
        if ($cookies.authenticated) {
            $location.path('/site');
            return;
        }
        var login = this;
        login.showAuthentication = $cookies.authenticated;
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
                loginData.login(login.username, login.password)
                    .then(function (data) {
                        $scope.lg.username.$setValidity(
                          'invalidUsernamePassword',
                          data
                        );
                        if (data) {
                            $cookies.authenticated = true;
                            $cookies.username = login.username;
                            $location.path('/site');
                        }
                    });
            }
        };
        login.doSignup = function () {
            $location.path('/signup');
        };
        function checkApi() {
            var url = '/api/dashboard';
            $http.get(url).success(function (data, status) {
                login.apiup = (status == 200);
            }).error(function (data, status) {
                login.apiup = false;
            });
        }
        checkApi();

        function getAppVersion(){
            var url = '/api/appinfo';
            $http.get(url).success(function (data, status) {
                console.log("appinfo:"+data);
                login.appVersion=data;
            }).error(function(data,status){
                console.log("appInfo:"+data);
                login.appVersion="0.0";
            });
        }
        getAppVersion();

    }
    app.controller('LoginController', inject.concat([LoginController]));
})();
