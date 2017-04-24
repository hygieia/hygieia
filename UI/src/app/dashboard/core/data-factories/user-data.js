(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('userData', userData);

    function userData($http) {
        var testDetailRoute = 'test-data/signup_detail.json';
        var usersRoute = '/api/users/';
        var adminRole = 'ROLE_ADMIN';

        return {
            getAllUsers: getAllUsers,
            promoteUserToAdmin: promoteUserToAdmin,
            demoteUserFromAdmin: demoteUserFromAdmin
        };


        // reusable helper
        function getPromise(route) {
            return $http.get(route).then(function (response) {
              console.log("Data="+ JSON.stringify(response.data));
                return response.data;
            });
        }

      function getAllUsers(){

          if(HygieiaConfig.local)
          {
            console.log("In local testing");
            return getPromise(testDetailRoute);
          }
          else
          {
        return $http.get(usersRoute);
      }
    }

    function promoteUserToAdmin(user) {
        var route = usersRoute + user.username + "/roles";
        var postData = {"userRole":adminRole};
        return $http.post(route, postData);
    }

    function demoteUserFromAdmin(user) {
      var route = usersRoute + user.username + "/roles/" + adminRole;
      return $http.delete(route);
    }

  }
})();
