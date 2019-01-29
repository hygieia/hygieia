(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('serviceAccountData', serviceAccountData);

    function serviceAccountData($http) {
        var testDetailRoute = 'test-data/signup_detail.json';
        var adminRoute = '/api/admin';

        return {
            getAllServiceAccounts: getAllServiceAccounts,
            createAccount: createAccount,
            updateAccount : updateAccount,
            deleteAccount : deleteAccount
         };


        // reusable helper
        function getPromise(route) {
            return $http.get(route).then(function (response) {
              console.log("Data="+ JSON.stringify(response.data));
                return response.data;
            });
        }

      function getAllServiceAccounts(){
          var route = adminRoute + "/allServiceAccounts";
          if(HygieiaConfig.local)
          {
            console.log("In local testing");
            return getPromise(testDetailRoute);
          }
          else
          {
        return $http.get(route);
      }
    }


        function createAccount(account) {
            var route = adminRoute + "/createAccount";
            return $http.post(route, account);
        }

        function updateAccount(account,id) {
            var route = adminRoute + "/updateAccount";
            return $http.post(route + '/' + id, account);
        }

        function deleteAccount(id){
            var route = adminRoute + "/deleteAccount";
            return $http.delete(route+"/"+id).then(function (response) {
                return response.data;
            });
        }

  }
})();
