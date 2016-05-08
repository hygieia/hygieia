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

        function getDataByAccount(type, accountNumber) {

            var deferred = $q.defer();

            var cloudDataRoute = '/api/cloud/' + type + '/details/account/';

            var route = (HygieiaConfig.local ? testDataRoute : cloudDataRoute) + accountNumber;
            $http.get(route)
                .success(function (data) {
                    deferred.resolve(data);
                })
                .error(function(error) {
                    deferred.reject(error);
                });

            return deferred.promise;
        }

        return {
            getAWSInstancesByAccount: getAWSInstancesByAccount,
            getAWSVolumeByAccount: getAWSVolumeByAccount
        };


        function getAWSInstancesByAccount(accountNumber) {
            return getDataByAccount('instance', accountNumber);

        }

        function getAWSVolumeByAccount(accountNumber) {
            return getDataByAccount('volume', accountNumber);
        }
  }
})();