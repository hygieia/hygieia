/**
 * Gets code quality related data
 */
(function () {
    'use strict';

    angular
        .module('devops-dashboard.core')
        .factory('cloudData', cloudData);

    function cloudData($http) {
        var testDetailRoute = 'test-data/serverdata.json';
        var caDetailRoute = '/api/cloud/';
        var tableDetailRoute = '/api/cloud/detailed';
        var authenticationRoute = '/api/cloud/authenticateUser';

        return {
            localTest: localTest,
            details: details,
            accessAuthentication: accessAuthentication,
            table: table
        };

        function localTest(params) {
            return $http.get(testDetailRoute, {params: params}).then(function (response) {
                return response.data;
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