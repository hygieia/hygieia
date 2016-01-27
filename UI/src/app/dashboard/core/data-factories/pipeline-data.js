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

        // get commit data for the given team collector item ids
        function commits(beginDate, endDate, collectorItemIds) {
            // make sure it's an array
            collectorItemIds = [].concat(collectorItemIds);

            // add our begin and end date
            var params = [
                'beginDate=' + beginDate,
                'endDate=' + endDate
            ];

            // add our collector items
            _(collectorItemIds).forEach(function(id) {
                params.push('collectorItemId=' + id);
            });

            var query = params.join('&');
            return $http.get(HygieiaConfig.local ? localRoute : pipelineRoute + '?' + query)
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();