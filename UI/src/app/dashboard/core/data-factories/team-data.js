/**
 * Gets code rally related data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('teamInventoryData', rallyData);

    function rallyData($http) {
        var testDetailRoute = 'test-data/rallyData.json';
        var caDetailRoute = 'api/teamInventory';
        return {
            details: details
        };
        function details(params) {
            return $http.get(HygieiaConfig.local ? testDetailRoute : caDetailRoute, { params: params })
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();