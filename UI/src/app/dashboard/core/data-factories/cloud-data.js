/**
 * Gets code quality related data
 */
(function () {
    'use strict';

    angular
        .module('devops-dashboard.core')
        .factory('cloudData', cloudData);

    function cloudData($http, $interpolate) {
        var aggregatedRoute = '/api/cloud/{{id}}/aggregate';
        var instanceDetailsRoute = '/api/cloud/{{id}}/details';
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
            var r = $interpolate(aggregatedRoute)(params);
            return $http.get(routeUrl(r), params).then(function (response) {
                return response.data.result;
            });
        }

        function details(params) {
            var r = $interpolate(instanceDetailsRoute)(params);
            return $http.get(routeUrl(r), params).then(function (response) {
                return response.data.result;
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
