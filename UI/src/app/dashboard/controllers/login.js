/**
 * Controller for performing authentication or signingup a new user */
(function () {
    'use strict';

    angular
        .module('devops-dashboard')
        .controller('LoginController', LoginController);


    LoginController.$inject = ['$scope', 'loginData', '$location', '$cookies', '$http'];
    function LoginController($scope, loginData, $location, $cookies, $http) {
        var login = this;

        // public variables
        login.showAuthentication = $cookies.authenticated;
        login.id= '';
        login.passwd= '';
        login.submitted= false;
        $scope.alerts = [];
        login.apiup=false;


      //public methods
        login.doLogin=doLogin;
        login.doSignup=doSignup;
        login.templateUrl = "app/dashboard/views/navheader.html";
        login.doCheckState = doCheckState;
        login.checkApi=checkApi;


        //function for closing alerts
        $scope.closeAlert = function (index) {
            $scope.alerts.splice(index, 1);
        };


        function doCheckState() {
            //Call the method to make sure api layer is up
            checkApi();
            if ($cookies.authenticated) {
                $location.path('/site');

            }
            else {
                $location.path('/');
            }
        }

      function doLogin(valid)
      {

          login.submitted=true;
        if(valid)
        {
            loginData.login(document.lg.loginfield.value, document.lg.passwordfield.value).then(processResponse);
        }
        else {
            $scope.alerts.splice(0, $scope.alerts.length);
            $scope.alerts.push({type: 'info', msg: "Please fill all the form fields correctly"});
        }

      }




      function processResponse(data) {


          console.log("Authentication is:"+data);

          if(data)
          {
            $cookies.authenticated=true;
              $cookies.username=document.lg.loginfield.value;

            $location.path('/site');

          }
          else {
              $scope.alerts.splice(0, $scope.alerts.length);
              $scope.alerts.push({type: 'danger', msg: 'Incorrect Username and Password please check'});
          }
      }

        function doSignup()
        {
            console.log("In signup");
            $location.path('/signup');
        }

        function checkApi()
        {
            var url = '/api/dashboard';

            $http.get(url)
                .success(function(data, status, headers, config) {

                    if(status == 200)
                    {
                        console.log("API layer up");
                        login.apiup=true;

                    }
                    //we will add explicit code to check if we we secure the api layer.
                    else
                    {
                        console.log("API layer down");

                    }

                })
                .error(function(data, status, headers, config) {

                   login.apiup=false;

                });
        }

    }
})();
