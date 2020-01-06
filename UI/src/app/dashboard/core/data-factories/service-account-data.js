(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('serviceAccountData', serviceAccountData);

    function serviceAccountData($http) {
        var testServiceAccounts = 'test-data/all_service_accounts.json';
        var adminRoute = '/api/admin';

        return {
            getAllServiceAccounts: getAllServiceAccounts,
            createAccount: createAccount,
            updateAccount : updateAccount,
            deleteAccount : deleteAccount
         };

      function getAllServiceAccounts(){
          var route = adminRoute + "/allServiceAccounts";
          return $http.get(HygieiaConfig.local ? testServiceAccounts : route);
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
