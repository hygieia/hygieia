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

        return {
            getConfigItemList: getConfigItemList,
        };
        function getConfigItemList(type, params){
            return $http.get(HygieiaConfig.local ? testConfigItemRoute : dashboardConfigItemListRoute + '/' + type,{params: params}).then(function (response) {
                return response.data;
            });
        }



    }
})();
