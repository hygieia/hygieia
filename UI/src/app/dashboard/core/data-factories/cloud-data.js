/**
 * Created by nmande on 4/12/16.
 */

(function () {
    'use strict';


    angular
        .module(HygieiaConfig.module + '.core')
        .factory('cloudData', cloudData);

    function cloudData($http, $q) {

        var testDataRoute = 'asv_data.json';
        var cloudInstanceDataRoute = '/api/cloud/instance/details/account';

        return {
            getAWSInstancesByAccount: getAWSInstancesByAccount
        };


        function getAWSInstancesByAccount(value) {

            var deferred = $q.defer();

            var route = (HygieiaConfig.local ? testDataRoute : cloudInstanceDataRoute) + "/" + value;
            $http.get(route)
                .success(function (data) {
                    deferred.resolve(data);
                })
                .error(function(error) {
                    deferred.reject(error);
                });

            return deferred.promise;
        }
  }
})();