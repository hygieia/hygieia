/**
 * Gets deploy related data
 */
(function () {
    'use strict';

    angular
        .module('devops-dashboard.core')
        .factory('deployData', deployData);

    function deployData($http) {
        var testDetailRoute = 'test-data/deploy_detail.json';
        var deployDetailRoute = '/api/deploy/status/';

        return {
            details: details
        };

        function details(componentId) {
            return $http.get(localTesting ? testDetailRoute : deployDetailRoute + componentId)
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();