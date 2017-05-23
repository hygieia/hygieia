/**
 * Gets code quality related data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('libraryPolicyData', libraryPolicyAnalysisData);

    function libraryPolicyAnalysisData($http) {
        var testLibraryPolicyDetailRoute = 'test-data/libary-policy.json';
        var libraryPolicyDetailRoute = '/api/libraryPolicy';

        return {
            libraryPolicyDetails: libraryPolicyDetails
        };

        // get the latest library policy data for the component
        function libraryPolicyDetails(params) {
            return $http.get(HygieiaConfig.local ? testLibraryPolicyDetailRoute : libraryPolicyDetailRoute, { params: params })
                .then(function (response) { return response.data; });
        }
    }
})();
