/**
 * Gets code repo related data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('pullRepoData', pullRepoData);

    function pullRepoData($http) {
        var testDetailRoute = 'test-data/commit_detail.json';
        var caDetailRoute = '/api/gitrequests/type/pull/state/all';

        var testPendingPullRequestRoute = 'test-data/pending-pull-request.json';
        var buildPendingPullRequestRoute = '/api/pending-pull-requests/';

        return {
            details: details,
            pendingPullRequestsDetails: pendingPullRequestsDetails
        };

        // get 15 days worth of commit data for the component
        function details(params) {
            return $http.get(HygieiaConfig.local ? testDetailRoute : caDetailRoute, {params: params})
                .then(function (response) {
                    return response.data;
                });
        }


        function pendingPullRequestsDetails(collectorId, repoName) {
            console.log("collector id = ", collectorId, " et repo name = ", repoName);
            return $http.get(HygieiaConfig.local ? testPendingPullRequestRoute : buildPendingPullRequestRoute + repoName)
                .then(function (response) {
                    console.log("reponse requete = ", response);
                    return response.data;
                });
        }
    }
})();
