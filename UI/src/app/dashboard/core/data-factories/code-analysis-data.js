/**
 * Gets code quality related data
 */
(function () {
    'use strict';

    angular
        .module('devops-dashboard.core')
        .factory('codeAnalysisData', codeAnalysisData);

    function codeAnalysisData($http) {
        var testDetailRoute = 'test-data/ca_detail.json';
        var caDetailRoute = '/api/quality/';

        return {
            details: details
        };

        // get the latest code quality data for the component
        function details(params) {
            return $http.get(localTesting ? testDetailRoute : caDetailRoute, { params: params })
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();