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
        var LoginDetailRoute = '/api/login';
        var LdapLoginDetailRoute = '/api/login/ldap';

        return {
            login: login,
            loginLdap: loginLdap
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

      function login(id, password) {
        return callLogin(LoginDetailRoute, id, password);
      }

      function loginLdap(id, password) {
        return callLogin(LdapLoginDetailRoute, id, password);
      }

      function callLogin(route, id, passwd){
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

        return $http({
          method: 'POST',
          url: route,
          headers: {'Content-Type': 'application/x-www-form-urlencoded'},
          data: postData,
          transformRequest: function(data) {
              var str = [];
              for(var p in data)
              str.push(encodeURIComponent(p) + "=" + encodeURIComponent(data[p]));
              return str.join("&");
          }
        }).then(function(response) {
          return response;
        },
          function(response) {
            return response;
        })
      }
    }
  }
})();
