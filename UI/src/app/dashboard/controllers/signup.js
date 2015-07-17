/**
 * Controller for performing signup a new user */
(function () {
    'use strict';

    angular
        .module('devops-dashboard')
        .controller('SignupController', SignupController);

    SignupController.$inject = ['$scope','signupData', '$location', '$cookies'];
    function SignupController($scope, signupData, $location, $cookies) {
        var signup = this;

        // public variables
        signup.id = '';
        signup.passwd='';
        signup.templateUrl = "app/dashboard/views/navheader.html";
        signup.userCreated = false;
        $scope.alerts = [];


        $scope.closeAlert = function(index) {
            $scope.alerts.splice(index, 1);
            if (signup.userCreated) {
                $location.path("/");
            }
        };

      //public methods
      signup.doSignup=doSignup;
        signup.doReset = doReset;

        function doSignup(valid)
      {
          console.log("Submit was pressed and form was" + valid);
          if (valid) {
              signupData.signup(document.suf.myusername.value, document.suf.mypassword.value).then(processResponse);
          }
          else {
              $scope.alerts.splice(0, $scope.alerts.length);
              $scope.alerts.push({type: 'info', msg: "Please fill all the form fields correctly"});
          }
      }

        function doReset() {
            signup.id = '';
            signup.password = '';
            signup.passwordConfirm = '';
        }

      function processResponse(data) {
          console.log(data);
          if (data == 'User already Exist') {
              $scope.alerts.splice(0, $scope.alerts.length);

              $scope.alerts.push({type: 'danger', msg: data});
          }
          else {
              $scope.alerts.splice(0, $scope.alerts.length);
              $scope.alerts.push({type: 'success', msg: data});
              signup.userCreated = true;

          }



      }

    }
})();
