/**
 * Controller for performing signup a new user */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('SignupController', SignupController);

    SignupController.$inject = ['$scope', 'signupData', '$location', 'tokenService'];
    function SignupController($scope, signupData, $location, tokenService) {
        var signup = this;

        // public variables
        signup.id = '';
        signup.passwd = '';
        signup.templateUrl = "app/dashboard/views/navheader.html";
        signup.userCreated = false;


        $scope.closeAlert = function (index) {

            if (signup.userCreated) {
                $location.path("/");
            }
        };

        //public methods
        signup.doSignup = doSignup;
        signup.doLogin = doLogin;

        function doSignup(valid) {
            if (valid) {
                signupData.signup(document.suf.id.value, document.suf.password.value).then(processResponse);
            }
        }

        function doLogin() {
            $location.path('/');
        }

        function processResponse(data) {
          if(data.status === 200) {
            tokenService.setToken(data.headers()['x-authentication-token']);
            $location.path('/');
          } else {
            $scope.suf.id.$setValidity('exists', false);
            signup.userCreated = false;
          }

        }

    }
})();
