/**
 * Api service for the monitor widget
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('monitorData', monitorData);

    function monitorData($http) {
        var monitorRoute = '/api/dashboard/';
        var serviceRoute = '/api/service/';

        var testingDetailRoute = 'test-data/monitor.json';
        var testingSearchRoute = 'test-data/monitor_config.json';

        var mappedStatusValues = {
            1: 'Ok',
            2: 'Warning',
            3: 'Alert'
        };

        return {
            details: details,
            search: search,
            createService: createService,
            updateService: updateService,
            deleteService: deleteService,
            createDependentService: createDependentService,
            deleteDependentService: deleteDependentService
        };

        // helper methods
        function getBaseRoute(dashboardId) {
            return monitorRoute + dashboardId + '/';
        }

        function getServiceRoute(dashboardId) {
            return getBaseRoute(dashboardId) + 'service/';
        }

        function getDependentServiceRoute(dashboardId) {
            return getBaseRoute(dashboardId) + 'dependent-service/';
        }

        // get all registered services
        function search() {
            return $http.get(HygieiaConfig.local ? testingSearchRoute : serviceRoute)
                .then(function (response) {
                    return response.data;
                });
        }

        // get services for a given dashboard
        function details(dashboardId) {
            return $http.get(HygieiaConfig.local ? testingDetailRoute : getServiceRoute(dashboardId))
                .then(function (response) {
                    return response.data;
                });
        }

        // add a new service name. name must be sent with quotes around it
        function createService(dashboardId, name) {
            return $http.post(HygieiaConfig.local ? testingDetailRoute : getServiceRoute(dashboardId), JSON.stringify(name))
                .then(function (response) {
                    return response.data;
                });
        }

        function updateService(dashboardId, service) {
            // create a copy so we don't modify the original
            service = angular.copy(service);

            var serviceId = service.id;
            if (serviceId) {
                delete service.id;
            }

            // try to map the status value back to what the api expects
            service.status = mappedStatusValues[service.status] || service.status;
            return $http.put(HygieiaConfig.local ? testingDetailRoute : getServiceRoute(dashboardId) + serviceId, service)
                .then(function (response) {
                    return response.data;
                });
        }

        // delete a service. will only work for the dashboard that created it
        function deleteService(dashboardId, serviceId) {
            return $http.delete(HygieiaConfig.local ? testingDetailRoute : getServiceRoute(dashboardId) + serviceId)
                .then(function (response) {
                    return response.data;
                });
        }

        // add a new dependent service on the dashboard
        function createDependentService(dashboardId, serviceId) {
            return $http.post(HygieiaConfig.local ? testingDetailRoute : getDependentServiceRoute(dashboardId) + serviceId, {})
                .then(function (response) {
                    return response.data;
                });
        }

        // delete an existing dependent service
        function deleteDependentService(dashboardId, serviceId) {
            return $http.delete(HygieiaConfig.local ? testingDetailRoute : getDependentServiceRoute(dashboardId) + serviceId)
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();