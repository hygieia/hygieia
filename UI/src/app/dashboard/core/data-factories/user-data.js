(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('userData', userData);

    function userData($http) {
        var testDetailRoute = 'test-data/signup_detail.json';
        var testAllUsersRoute = 'test-data/all_users.json'
        var testApitokens = 'test-data/apitokens.json';
        var adminRoute = '/api/admin';
        var userRoute = '/api/users';

        return {
            getAllUsers: getAllUsers,
            promoteUserToAdmin: promoteUserToAdmin,
            demoteUserFromAdmin: demoteUserFromAdmin,
            createToken: createToken,
            apitokens: apitokens,
            deleteToken: deleteToken,
            updateToken: updateToken
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
            return getPromise(testAllUsersRoute);
            // return getPromise(testDetailRoute);
          } else
          {  
            return $http.get(userRoute);
          }
    }

    function promoteUserToAdmin(user) {
        var route = adminRoute + "/users/addAdmin";
        return $http.post(route, user);
    }

    function demoteUserFromAdmin(user) {
      var route = adminRoute + "/users/removeAdmin";
      return $http.post(route, user);
    }

    function createToken(apitoken) {
        var route = adminRoute + "/createToken";
        return $http.post(route, apitoken);
    }

    function apitokens() {
      if(HygieiaConfig.local) {
        console.log("In local testing");
        return getPromise(testApitokens);
      } else {
        var route = adminRoute + "/apitokens";
        return $http.get(route);
      }
    }

    function deleteToken(id) {
        var route = adminRoute + "/deleteToken";
        return $http.delete(route + '/' + id)
            .then(function (response) {
                return response.data;
            });
    }
    function updateToken(apiToken, id) {
        var route = adminRoute + "/updateToken";
        return $http.post(route + '/' + id, apiToken);
    }
  }
})();
