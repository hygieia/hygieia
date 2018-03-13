/**
 * Gets score related data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('scoreData', scoreData);

    function scoreData($http) {
        var testDetailRoute = 'test-data/score_detail.json';
        var scoreDetailRoute = '/api/score/metric/';

        return {
            details: details
        };

        function details(dashboardId) {
            return $http.get(HygieiaConfig.local ? testDetailRoute : scoreDetailRoute + dashboardId)
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();
