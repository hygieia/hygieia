/**
 * Controller for performing signup a new user */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('SignupController', SignupController);

    SignupController.$inject = ['$scope', 'signupData', '$location', '$cookies'];
    function SignupController($scope, signupData, $location, $cookies) {
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
            var exists = data == 'User already exists';
            $scope.suf.id.$setValidity('exists', !exists);
            signup.userCreated = !exists;
        }

    }
})();
