/**
 * Gets build related data
 */
(function () {
    'use strict';

    angular
        .module('devops-dashboard.core')
        .factory('buildData', buildData);

    function buildData($http) {
        var testDetailRoute = 'test-data/build_detail.json';
        var buildDetailRoute = '/api/build/';

        return {
            details: details
        };

        // search for current builds
        function details(params) {
            return $http.get(localTesting ? testDetailRoute : buildDetailRoute, { params: params })
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();