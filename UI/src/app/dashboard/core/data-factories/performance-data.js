/**
 * Gets code repo related data
 */

(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('performanceData', performanceData);

    function performanceData($http) {
        var paApplicationPerformanceRoute = '/api/performance/application';
        var paInfrastructurePerformanceRoute = '/api/performance/infrastructure';
        var testApplicationPerformanceRoute = 'test-data/ad_app_perfoamance.json';
        var testInfrastructurePerformanceRoute = 'test-data/ad_infra_performance.json';

        return {
            appPerformance: appPerformance,
            infraPerformance: infraPerformance
        };

        function appPerformance(params) {
            return $http.get(HygieiaConfig.local ? testApplicationPerformanceRoute : paApplicationPerformanceRoute, {params: params})
                .then(function (response) {
                    return response.data;
                });
        }


        function infraPerformance(params) {
            return $http.get(HygieiaConfig.local ? testInfrastructurePerformanceRoute : paInfrastructurePerformanceRoute, {params: params})
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();
