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
        var caDetailRoute = '/api/cloud/aggregated';
        var tableDetailRoute = '/api/cloud/details';
        var authenticationRoute = '/api/cloud/config';
        var saveConfigRoute = '/api/cloud/config';


        return {
            //localDetails: localDetails,
            //localTable: localTable,
            details: details,
            accessAuthentication: accessAuthentication,
            table: table,
            saveConfig: saveConfig
        };
/**
        function localDetails(params) {
            return $http.get(testDetailsRoute, {params: params}).then(function (response) {
                return response.data;
            })
        }
 **/
/**
        function localTable(params) {
            return $http.get(testTableRoute, {params: params}).then(function (response) {
                return response.data.result;
            })
        }
 **/

        // get the latest code quality data for the component
        function details(params) {
            return $http.get(localTesting ? testDetailsRoute : caDetailRoute, {params: params}).then(function (response) {
                return response.data.result;
            })
        }
//localTesting ? testDetailRoute : buildDetailRoute
        function table(params) {
            return $http.get(localTesting ? testTableRoute : tableDetailRoute, {params: params}).then(function (response) {
                return response.data.result;
            })
        }

        function accessAuthentication(params) {
            return $http.post(authenticationRoute, {params: params}).then(function (response) {
                return response.data;
            });
        }

        function saveConfig(params) {
            console.log(params);
            return $http.post(saveConfigRoute, params).then(function (response) {
                return response.data;
            });
        }
    }
})();