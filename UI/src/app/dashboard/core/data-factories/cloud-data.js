/**
 * Created by nmande on 4/12/16.
 */

(function () {
    'use strict';


    angular
        .module(HygieiaConfig.module + '.core')
        .factory('cloudData', cloudData)
        .factory('cloudHistoryData',cloudHistoryData);

    function cloudHistoryData($http, $q) {
        var testDataRoute='instance_history.json';

        function getInstanceHistoryDataByAccount(accountNumber){

            var historyDeffered = $q.defer();

            var cloudHistoryDataRoute = '/api/cloud/instance/history/account/';

            var historyRoute = (HygieiaConfig.local ? testDataRoute : cloudHistoryDataRoute) + accountNumber;
            $http.get(historyRoute)
                .success(function (data) {
                    historyDeffered.resolve(data);
                })
                .error(function(error) {
                    historyDeffered.reject(error);
                });
            return historyDeffered.promise;

        }
        return {
            getInstanceHistoryDataByAccount: getInstanceHistoryDataByAccount
        };

    }


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
            getAWSVolumeByAccount: getAWSVolumeByAccount,
            getAWSSubnetsByAccount: getAWSSubnetsByAccount
        };


        function getAWSInstancesByAccount(accountNumber) {
            return getDataByAccount('instance', accountNumber);

        }

        function getAWSVolumeByAccount(accountNumber) {
            return getDataByAccount('volume', accountNumber);
        }

        function getAWSSubnetsByAccount(accountNumber) {
            return getDataByAccount('subnet', accountNumber);
        }
  }
})();