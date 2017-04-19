/**
 * Api service for the monitor widget
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('pipelineData', pipelineData);

    function pipelineData($http) {
        var pipelineRoute = '/api/pipeline/';
        var localRoute = 'test-data/pipeline-commits.json';

        return {
            commits: commits
        };

        // get commit data for the given team collector item ids.
        // can pass a single collector item id or an array
        function commits(beginDate, endDate, collectorItemIds) {
            // make sure it's an array
            collectorItemIds = [].concat(collectorItemIds);

            // add our begin and end date
            var params = {
                beginDate: beginDate,
                endDate: endDate,
                collectorItemId: collectorItemIds
            };

            return $http.get(HygieiaConfig.local ? localRoute : pipelineRoute, { params: params })
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();