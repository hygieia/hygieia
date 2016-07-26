/**
 * Api service for the aws status widget
 */
(function () {
    'use strict';
   
    angular
        .module(HygieiaConfig.module + '.core')
        .factory('awsStatusData', awsStatusData);
    
    function awsStatusData($http) {
        var awsStatusRoute = '/api/dashboard/';
        var allStatusesRoute = '/api/awsStatus';
        
        return {
            details: details,
            search: search,
            createAwsStatus: createAwsStatus,
            updateAwsStatus: updateAwsStatus,
            deleteAwsStatus: deleteAwsStatus
        };
        
        //helper methods
        function getBaseRoute(dashboardId) {
            return awsStatusRoute + dashboardId + '/';
        }
        
        function getAwsStatusRoute(dashboardId) {
            return getBaseRoute(dashboardId) + 'awsStatus/'
        }

        // GET: all registered statuses
        function search() {
            return $http.get(allStatusesRoute)
                .then(function (response) {
                    return response.data;
                })
        }

        // GET: all statuses for a given dashboard
        function details(dashboardId) {
            return $http.get(getAwsStatusRoute(dashboardId))
                .then(function (response) {
                    return response.data;
                })
        }

        // POST: create a new status for given dashboard.
        function createAwsStatus(dashboardId, name, url) {
            return $http.post(getAwsStatusRoute(dashboardId), {name: name, url: url})
                .then(function (response) {
                    return response.data;
                })
        }

        // PUT: update a status with new data
        function updateAwsStatus(dashboardId, status) {
            status = angular.copy(status);

            var statusId = status.id;
            if (statusId) {
                delete status.id;
            }

            return $http.put(getAwsStatusRoute(dashboardId) + statusId, status)
                .then(function (response) {
                    return response.data;
                })
        }

        // DELETE: remove a specific status
        function deleteAwsStatus(dashboardId, statusId) {
            return $http.delete(getAwsStatusRoute(dashboardId) + statusId)
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();