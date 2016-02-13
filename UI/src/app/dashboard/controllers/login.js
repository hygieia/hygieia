/**
 * Controller for performing authentication or signingup a new user */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('LoginController', LoginController);


    LoginController.$inject = ['$scope', 'loginData', '$location', '$cookies', '$http'];
    function LoginController($scope, loginData, $location, $cookies, $http) {
        var login = this;

        // public variables
        login.showAuthentication = $cookies.authenticated;
        login.id = '';
        login.passwd = '';
        login.apiup = false;
        login.invalidUsernamePassword = false;


        //public methods
        login.doLogin = doLogin;
        login.doSignup = doSignup;
        login.templateUrl = "app/dashboard/views/navheader.html";
        login.doCheckState = doCheckState;
        login.checkApi = checkApi;


        function doCheckState() {
            if ($cookies.authenticated) {
                $location.path('/site');
                return;
            }

            //Call the method to make sure api layer is up
            checkApi();
        }

        function doLogin(valid) {
            if (valid) {
                loginData.login(document.lg.id.value, document.lg.password.value).then(processResponse);
            }
        }


        function processResponse(data) {

            console.log("Authentication is:" + data);

            $scope.lg.id.$setValidity('invalidUsernamePassword', data);

            if (data) {

                $cookies.authenticated = true;
                $cookies.username = document.lg.id.value;

                $location.path('/site');
            }
        }

        function doSignup() {
            console.log("In signup");
            $location.path('/signup');
        }

        function checkApi() {
            var url = '/api/dashboard';

            $http.get(url)
                .success(function (data, status, headers, config) {

                    if (status == 200) {
                        console.log("API Connectivity");
                        login.apiup = true;
                    }
                    // we will add explicit code to check if we we secure the api layer.
                    else {
                        console.log("API layer down");
                    }
                })
                .error(function (data, status, headers, config) {

                    login.apiup = false;
                });
        }

    }
})();
