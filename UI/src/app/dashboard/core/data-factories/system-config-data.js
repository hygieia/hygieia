/**
 * Gets system config data
 */
( function() {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('systemConfigData', systemConfigData);
	
    function systemConfigData($http, $cacheFactory) {
        var testRoute = 'test-data/system_config.json';
        var prodRoute = '/api/config/';

        return {
            config: config
        };

        // search for current builds
        function config() {
            return $http.get(HygieiaConfig.local ? testRoute : prodRoute )
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();
