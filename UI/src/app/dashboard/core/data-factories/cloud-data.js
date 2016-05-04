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
            getAWSGlobalData: getAWSGlobalData,
            getAWSInstancesByAccount: getAWSInstancesByAccount
        };


        function getAWSGlobalData() {


            /* return {
             "compute": {
             "ec2Instances": 3015,
             "running": 1900,
             "stopped": 300,
             "excluded": 910
             },
             "s3": {
             "s3Buckets": 9000,
             "encrypted": 35,
             "tagged": 45,
             "compliant": 54
             }
             }; */
        }

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