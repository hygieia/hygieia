/**
 * Controller for performing signup a new user */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('SignupController', SignupController);

    SignupController.$inject = ['$scope', 'authService', '$location'];
    function SignupController($scope, authService, $location) {
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
                authService.register({username:document.suf.id.value, password:document.suf.password.value}).then(processSuccessfulResponse, processFailureResponse);
            }
        }

        function doLogin() {
            $location.path('/login');
        }

        function processSuccessfulResponse(response) {
            $location.path('/');
        }

        function processFailureResponse(response) {
          $scope.suf.id.$setValidity('exists', false);
          signup.userCreated = false;
        }

        $scope.resetUsernameFieldValidity = function () {
          $scope.suf.id.$setValidity('exists', true);
        }

    }
})();
