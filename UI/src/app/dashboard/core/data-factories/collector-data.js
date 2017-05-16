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
        var itemByComponentRoute = '/api/collector/item/component/';
        var itemsByTypeRoute = '/api/collector/item/type/';
        var collectorsByTypeRoute = '/api/collector/type/';
        var encryptRoute = "/api/encrypt/";

        return {
            itemsByType: itemsByType,
            createCollectorItem: createCollectorItem,
            getCollectorItem : getCollectorItem,
            collectorsByType: collectorsByType,
            encrypt: encrypt,
            getCollecterItem:getCollectorItem

        };

        function getCollectorItem(id) {
            return $http.get(itemRoute + '/'+id).then(function (response) {
                return response.data;
            });
        }

        function itemsByType(type, params) {
            return $http.get(itemsByTypeRoute + type, {params: params}).then(function (response) {
                return response.data;
            });
        }

        function createCollectorItem(collectorItem) {
            return $http.post(itemRoute, collectorItem);
        }


        function getCollectorItem(item, type) {
            return $http.get(itemByComponentRoute + item + '?type=' + type).then(function (response) {
                return response.data;
            });
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
