/**
 * Cmdb and cmdb item data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('cmdbData', cmdbData);

    function cmdbData($http) {
        var testConfigItemRoute = '';
        var dashboardConfigItemListRoute = '/api/cmdb/configItem';
        var configurationItemAppId = "";
        var configurationItemComponentId = "";
        return {
            getConfigItemList: getConfigItemList,
            setConfigItemAppId: setConfigItemAppId,
            getConfigItemAppId: getConfigItemAppId,
            getConfigItemComponentId: getConfigItemComponentId,
            setConfigItemComponentId: setConfigItemComponentId

        };
        function getConfigItemList(type, params){
            return $http.get(HygieiaConfig.local ? testConfigItemRoute : dashboardConfigItemListRoute + '/' + type,{params: params}).then(function (response) {
                return response.data;
            });
        }
        function setConfigItemAppId(id){
            configurationItemAppId = id;
        }

        function getConfigItemAppId(configurationItemApp){
            var value = null;
            if(configurationItemApp){
                value = configurationItemAppId;
            }
            return value;
        }

        function setConfigItemComponentId(id){
            configurationItemComponentId = id;
        }

        function getConfigItemComponentId(configurationItemComponent){
            var value = null;
            if(configurationItemComponent){
                value = configurationItemComponentId;
            }
            return value;
        }

    }
})();
