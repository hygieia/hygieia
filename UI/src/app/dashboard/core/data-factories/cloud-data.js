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
        var caDetailRoute = '/api/cloud/';
        var tableDetailRoute = '/api/cloud/detailed';
        var authenticationRoute = '/api/cloud/authenticateUser';


        return {
            localDetails: localDetails,
            localTable: localTable,
            details: details,
            accessAuthentication: accessAuthentication,
            table: table
        };

        function localDetails(params) {
            return $http.get(testDetailsRoute, {params: params}).then(function (response) {
                return response.data;
            })
        }

        function localTable(params) {
            return $http.get(testTableRoute, {params: params}).then(function (response) {
                return response.data.result;
            })
        }

        // get the latest code quality data for the component
        function details(params) {
            return $http.get(caDetailRoute, {params: params}).then(function (response) {
                return response.data.result;
            })
        }

        function table(params) {
            return $http.get(tableDetailRoute, {params: params}).then(function (response) {
                return response.data.result;
            })
        }

        function accessAuthentication(params) {
            return $http.post(authenticationRoute, {params: params}).then(function (response) {
                return response.data;
            });
        }
    }
})();