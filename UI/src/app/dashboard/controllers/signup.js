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
                authService.register({username:document.suf.id.value, password:document.suf.password.value}).then(processResponse);
            }
        }

        function doLogin() {
            $location.path('/');
        }

        function processResponse(response) {
          if(response.status === 200) {
            $location.path('/');
          } else {
            $scope.suf.id.$setValidity('exists', false);
            signup.userCreated = false;
          }

        }

    }
})();
