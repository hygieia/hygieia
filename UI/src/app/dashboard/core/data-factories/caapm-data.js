/**
 * Gets build related data
 */
(function () {
    'use strict';

    angular
    .module(HygieiaConfig.module + '.core')
        .factory('caapmData', caapmData);

    function caapmData($http) {
        var testDetailRoute = 'test-data/caapm_detail.json';
        var caapmDataRoute = '/api/getAlertsByManageModuleName/';

        return {
            details: details
        };

        // search for current builds
        function details(type) {
            return $http.get(HygieiaConfig.local ? testDetailRoute : caapmDataRoute + type)
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();