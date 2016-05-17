/**
 * Gets code repo related data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('issueClosedRepoData', issueClosedRepoData);

    function issueClosedRepoData($http) {
        var testDetailRoute = 'test-data/commit_detail.json';
        var caDetailRoute = '/api/issuesClosed';

        return {
            details: details
        };

        // get 15 days worth of commit data for the component
        function details(params) {
            return $http.get(HygieiaConfig.local ? testDetailRoute : caDetailRoute, { params: params })
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();
