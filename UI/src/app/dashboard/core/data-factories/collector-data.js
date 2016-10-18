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
        var encryptRoute = "/api/encrypt/";

        return {
            itemsByType: itemsByType,
            createCollectorItem: createCollectorItem,
            collectorsByType: collectorsByType,
            encrypt: encrypt
        };

        function itemsByType(type) {
            return $http.get(itemsByTypeRoute + type).then(function (response) {
                return response.data;
            });
        }

        function createCollectorItem(collectorItem) {
            return $http.post(itemRoute, collectorItem);
        }

        function collectorsByType(type) {
            return $http.get(collectorsByTypeRoute + type).then(function (response) {
                return response.data;
            });
        }

        function encrypt(message) {
            return $http.get(encryptRoute + message).then(function (response) {
                return response.data;
            });
        }
    }
})();