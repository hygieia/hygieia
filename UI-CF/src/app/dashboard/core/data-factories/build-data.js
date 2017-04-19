/**
 * Gets build related data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('buildData', buildData);

    function buildData($http) {
        var testDetailRoute = 'test-data/build_detail.json';
        var buildDetailRoute = '/api/build/';

        return {
            details: details
        };

        // search for current builds
        function details(params) {
            return $http.get(HygieiaConfig.local ? testDetailRoute : buildDetailRoute, { params: params })
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();