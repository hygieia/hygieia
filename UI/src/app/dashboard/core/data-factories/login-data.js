/**
 * Gets build related data
 */
(function () {
    'use strict';

    angular
        .module('devops-dashboard.core')
        .factory('loginData', loginData);

    function loginData($http) {
        var testDetailRoute = 'test-data/login_detail.json';
        var LoginDetailRoute = '/api/authenticateUser/';

        return {
            login: login
        };


        // reusable helper
        function getPromise(id,passwd,route) {
          var postData={
              'id': id,
              'passwd': passwd
            };
            return $http.get(route).then(function (response) {
              console.log("Data="+ JSON.stringify(response.data));
                return response.data;
            });
        }

      function login(id,passwd){
        var postData={
    				'username': id,
    				'password': passwd
    			};
          if(localTesting)
          {
            console.log("In local testing");
            return getPromise(id,passwd,testDetailRoute);
          }
          else
          {
        return $http.post(LoginDetailRoute,postData).then(function (response) {
            return response.data;
        });
      }
    }
  }
})();
