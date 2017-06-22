/**
 * Gets branches related data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('branchesData', branchesData);

    function branchesData($http) {
        var testDetailRoute = 'test-data/branches.json';
        var branchDetailRoute = '/api/collector/item';

        return {
            details: details
        };

        // search for current branches
        function details(collectorItemId) {
            return $http.get(HygieiaConfig.local ? testDetailRoute : branchDetailRoute + '/' + collectorItemId)
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();
