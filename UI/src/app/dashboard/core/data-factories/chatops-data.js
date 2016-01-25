/**
 * Gets code repo related data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('chatOpsData', chatOpsData);

    function chatOpsData($http) {
        var testDetailRoute = 'test-data/chatops-hipchat.json';
        return {
            details: details
        };

        function details(serviceUrl) {
            return $http.get(HygieiaConfig.local ? testDetailRoute : serviceUrl).then(function (response) {

                return response.data;
            }, function (response) {
                return response.data;
            });
        }


    }

})();