/**
 * Api service for the monitor widget
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('wlmonitorData', wlmonitorData);

    function wlmonitorData($http) {

        var testDetailRoute = 'test-data/wlmonitor-details.json';
        var configDetails = 'test-data/wlmonitor-config.json';
        var viewDetailRoute = '/api/getAllServersByEnvName/';
        var dashboardRoute= '/api/wlmonitor/widget/';
        return {
            details: details,
            itemsByType: itemsByType,
            saveWlmonitorDetails : saveWlmonitorDetails
        };

        // for test need to remove
        function itemsByType(dashboardId) {
            return $http.get(HygieiaConfig.local ? configDetails : configDetails)
                .then(function (response) {
                    return response.data;
                });
        }


        function details(envName) {
            return $http.get(HygieiaConfig.local  ? testDetailRoute : viewDetailRoute + envName )
                .then(function (response) {
                    return response.data;
                });
        }


        function saveWlmonitorDetails(widget) {
            // create a copy so we don't modify the original
            widget = angular.copy(widget);
            var vMonitorWidget = {};
            vMonitorWidget.selectedCollectorItemIds = widget.allselectedCollectorItemIds
            vMonitorWidget.collectorItemIds = widget.collectorItemIds;
            var widgetId = widget.id;

            if (widgetId) {
                // remove the id since that would cause an api failure
                delete widget.id;
            }

            var route = $http.post(dashboardRoute+ widget.componentId, vMonitorWidget);

            return route.then(function (response) {
                return response.data;
            });
        }
    }
})();