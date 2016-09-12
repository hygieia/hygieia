/**
 * Api service for the aws status widget
 */
(function () {
    'use strict';
   
    angular
        .module(HygieiaConfig.module + '.core')
        .factory('monitor2Data', monitor2Data);
    
    function monitor2Data($http) {
        var monitor2Route = '/api/dashboard/';
        var allStatusesRoute = '/api/monitor2';
        
        return {
            details: details,
            search: search,
            createMonitor2: createMonitor2,
            updateMonitor2: updateMonitor2,
            deleteMonitor2: deleteMonitor2,
            getMonitor2Status: getMonitor2Status
        };
        
        //helper methods
        function getBaseRoute(dashboardId) {
            return monitor2Route + dashboardId + '/';
        }
        
        function getMonitor2Route(dashboardId) {
            return getBaseRoute(dashboardId) + 'monitor2/'
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
            return $http.get(getMonitor2Route(dashboardId))
                .then(function (response) {
                    return response.data;
                })
        }

        // POST: create a new status for given dashboard.
        function createMonitor2(dashboardId, name, url) {
            return $http.post(getMonitor2Route(dashboardId), {name: name, url: url})
                .then(function (response) {
                    return response.data;
                })
        }

        // PUT: update a status with new data
        function updateMonitor2(dashboardId, status) {
            status = angular.copy(status);

            var statusId = status.id;
            if (statusId) {
                delete status.id;
            }

            return $http.put(getMonitor2Route(dashboardId) + statusId, status)
                .then(function (response) {
                    return response.data;
                })
        }

        // DELETE: remove a specific status
        function deleteMonitor2(dashboardId, statusId) {
            return $http.delete(getMonitor2Route(dashboardId) + statusId)
                .then(function (response) {
                    return response.data;
                });
        }

        function getMonitor2Status(dashboardId, monitor2Id, monitor2) {
            // TODO: CHANGE THIS TO CALL API ENDPOINT.
            return $http.head(monitor2.url)
                .then(function(response) {
                    if (response.status > 300) {
                        monitor2.status = 3
                    } else {
                        monitor2.status = 1;
                    }
                    return monitor2;
                }, function(response) {
                    monitor2.status = 3;
                    return monitor2;
                });
        }
    }
})();