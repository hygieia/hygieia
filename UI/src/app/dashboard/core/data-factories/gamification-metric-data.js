/**
 * Gets and stores Gamification metric data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('gamificationMetricData', gamifcationMetricData);

    function gamifcationMetricData($http) {
        // var testDetailRoute = 'test-data/commit_detail.json';
        var metricApiRoute = '/api/gamification/metrics';

        return {
            getMetricData: getMetricData,
            getEnabledMetricData: getEnabledMetricData,
            storeMetricData: storeMetricData
        };

        function getMetricData() {
            return $http({
                method: 'GET',
                url: metricApiRoute
            });
        }

        function getEnabledMetricData() {
            return $http({
                method: 'GET',
                url: metricApiRoute + '?enabled=true'
            });
        }

        function storeMetricData(metricData) {
            return $http({
                method: 'POST',
                url: metricApiRoute,
                data: metricData
            });
        }

    }
})();