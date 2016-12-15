/**
 * Controller for performing authentication or signingup a new user */
(function () {
    'use strict';
    var app = angular.module(HygieiaConfig.module)
    var inject = ['$window', '$http', '$location', '$scope', 'loginData']
    function LoginController($window, $http, $location, $scope, loginData) {
        if ($window.localStorage.token) {
            $location.path('/site');
            return;
        }
        var login = this;
        login.showAuthentication = $window.sessionStorage.token;
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
                    .then(function (response) {
                        if (response.status == 200) {
                            $window.localStorage.token = response.headers()['x-authentication-token'];
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
