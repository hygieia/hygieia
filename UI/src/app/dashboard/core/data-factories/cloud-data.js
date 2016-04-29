/**
 * Created by nmande on 4/12/16.
 */

(function () {
    'use strict';


    angular
        .module(HygieiaConfig.module + '.core')
        .factory('cloudData', cloudData);

    function cloudData($http) {

        var testDataRoute = 'asv_data.json';
        var cloudDataRoute = '/api/asv/';

        return {
            getAccounts: getAccounts,
            getAWSGlobalData: getAWSGlobalData,
            getAWSInstancesByTag: getAWSInstancesByTag
        };

        function getAccounts() {
            return JSON.parse('[{ "name": "Development Account"},{ "name": "Production Account"}]');
            return $http.get(HygieiaConfig.local ? testDataRoute : cloudDataRoute)
                .then(function (response) {
                    return response.data[0].result;
                });
        }


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

        function getAWSInstancesByTag(tag, value) {
            return $http.get(HygieiaConfig.local ? testDataRoute : cloudDataRoute)
                .then(function (response) {
                    return response.data[0].result;
                });
        }
    }
})();