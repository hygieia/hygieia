/**
 * Gets code quality related data
 */
(function () {
    'use strict';

    angular
        .module('devops-dashboard.core')
        .factory('cloudData', cloudData);

    function cloudData($http) {
        var testDetailsRoute = 'test-data/aws_aggregate.json';
        var testTableRoute = 'test-data/aws_raw.json';
        var aggregatedRoute = '/api/cloud/aggregate';
        var instanceDetailsRoute = '/api/cloud/details';
        var authenticationRoute = '/api/cloud/config';
        var saveConfigRoute = '/api/cloud/config';


        return {
            //localDetails: localDetails,
            //localTable: localTable,
            aggregate: aggregate,
            details: details,
            saveConfig: saveConfig
        };


        // get the latest code quality data for the component
        function aggregate(params) {
            return $http.post(localTesting ? testDetailsRoute : aggregatedRoute, params).then(function (response) {
                return response.data.result;
            })
        }

        function details(params) {
            return $http.post(localTesting ? testTableRoute : instanceDetailsRoute, params).then(function (response) {
                return response.data.result;
            })
        }

        function saveConfig(params) {
            console.log(params);
            return $http.post(saveConfigRoute, params).then(function (response) {
                return response.data;
            });
        }
    }
})();