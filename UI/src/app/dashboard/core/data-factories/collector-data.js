/**
 * Collector and collector item data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('collectorData', collectorData);

    function collectorData($http, $q) {
        var itemRoute = '/api/collector/item';
        var itemsByTypeRoute = '/api/collector/item/type/';
        var collectorsByTypeRoute = '/api/collector/type/';

        return {
            itemsByType: itemsByType,
            createCollectorItem: createCollectorItem,
            collectorsByType: collectorsByType
        };

        function itemsByType(type) {
            return $http.get(itemsByTypeRoute + type).then(function (response) {
                return response.data;
            });
        }

        function createCollectorItem(collectorItem) {

            var deferred = $q.defer();
            $http.post(itemRoute, collectorItem).success(function (data) {
                    deferred.resolve(data);
                })
                .error(function(error) {
                    deferred.reject(error);
                });

            return deferred.promise;
        }

        function collectorsByType(type) {
            return $http.get(collectorsByTypeRoute + type).then(function (response) {
                return response.data;
            });
        }
    }
})();