/**
 * Gets code quality related data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('logRepoData', logAnalysisData);

    function logAnalysisData($http) {
        var testStaticDetailRoute = 'test-data/logs.json';
        var logStaticDetailsRoute = '/api/loganalysis';

        return {
            logDetails: logDetails
        };

        // get the latest code quality data for the component
        function logDetails(params) {
            return $http.get(HygieiaConfig.local ? testStaticDetailRoute : logStaticDetailsRoute, { params: params })
                .then(function (response) { return response.data; });
        }
    }
})();
