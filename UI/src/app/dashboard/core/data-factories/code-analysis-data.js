/**
 * Gets code quality related data
 */
(function () {
    'use strict';

    angular
        .module('devops-dashboard.core')
        .factory('codeAnalysisData', codeAnalysisData);

    function codeAnalysisData($http) {
        var testStaticDetailRoute = 'test-data/ca_detail.json';
        var testSecDetailRoute = 'test-data/ca-security.json';
        var caStaticDetailsRoute = '/api/quality/static-analysis';
        var caSecDetailsRoute = '/api/quality/security-analysis';

        return {
            staticDetails: staticDetails,
            securityDetails: securityDetails
        };

        // get the latest code quality data for the component
        function staticDetails(params) {
            return $http.get(localTesting ? testStaticDetailRoute : caStaticDetailsRoute, { params: params })
                .then(function (response) { return response.data; });
        }

        function securityDetails(params) {
            return $http.get(localTesting ? testSecDetailRoute : caSecDetailsRoute, { params: params })
                .then(function (response) { return response.data; });
        }
    }


})();
