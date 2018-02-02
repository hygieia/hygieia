/**
 * Gets deploy related data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('scoreData', scoreData);

    function scoreData($http) {
        var testDetailRoute = 'test-data/score_detail.json';
        var deployDetailRoute = '/api/score/metric/';

        return {
            details: details
        };

        function details(componentId) {
            return $http.get(HygieiaConfig.local ? testDetailRoute : deployDetailRoute + componentId)
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();
