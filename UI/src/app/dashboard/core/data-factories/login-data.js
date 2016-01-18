/**
 * Gets build related data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
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
                return response.data;
            });
        }

      function login(id,passwd){
        var postData={
    				'username': id,
    				'password': passwd
    			};
          if(HygieiaConfig.local)
          {
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
