(function () {
   'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('monitor2ConfigController', monitor2ConfigController);

    monitor2ConfigController.$inject = ['$scope', '$q', '$modalInstance', 'monitor2Data', 'modalData'];
    function monitor2ConfigController($scope, $q, $modalInstance, monitor2Data, modalData) {
        var ctrl= this;

        // request and process data
        monitor2Data.details(modalData.dashboard.id)
            .then(processDetailResponse);
        monitor2Data.search()
            .then(processSearchResponse);

        // local variables
        var deletedDashboardStatuses = [];
        ctrl.appName = modalData.dashboard.application.name;
        ctrl.newDashboardMonitor2es = [];
        ctrl.dashboardMonitor2es = [];
        ctrl.allMonitor2es = [];

        // methods
        ctrl.save = save;
        ctrl.deleteDashboardMonitor2 = deleteDashboardMonitor2;
        ctrl.addNewDashboardMonitor2 = addNewDashboardMonitor2;
        ctrl.deleteNewDashboardMonitor2 = deleteNewDashboardMonitor2;

        // Processes the response of from the server for a detail request.
        function processDetailResponse(response) {
            var worker = {
                getMonitor2es: getMonitor2es
            };

            function getMonitor2es(data, cb) {
                cb({dashboardMonitor2es: getDashboardMonitor2es(data.result.monitor2es)})
            }

            function getDashboardMonitor2es(monitor2es) {
                return monitor2es;
            }

            worker.getMonitor2es(response, workerDetailCallback);
        }

        // process the server response for search request
        function processSearchResponse(response) {
            var appName = modalData.dashboard.application.name;

            ctrl.allServices = _(response)
                .filter(function (item) {
                    return item.applicationName != appName;
                })
                .map(function (item) {
                    return {
                        id: item.id,
                        name: item.applicationName + ': ' + item.name
                    };
                })
                .value();
        }

        function workerDetailCallback(obj) {
            ctrl.dashboardMonitor2es = obj.dashboardMonitor2es;
        }

        // Add deleted item to list for saving.
        function deleteDashboardMonitor2(idx) {
            deletedDashboardStatuses.push(
                ctrl.dashboardMonitor2es.splice(idx, 1)[0]
            );
        }

        // add new item to list for saving.
        function addNewDashboardMonitor2() {
            ctrl.newDashboardMonitor2es.push({name: '', url: ''});
        }

        // delete a status that hasnt been saved yet.
        function deleteNewDashboardMonitor2(idx) {
            ctrl.newDashboardMonitor2es.splice(idx, 1);
        }

        function save() {
            var dashboardId = modalData.dashboard.id;
            var promises = [];

            function whereName(data) {
                return _(data).where(function (item) {
                    return item.name && item.name.length;
                });
            }

            _(deletedDashboardStatuses).forEach(function (item) {
                promises.push(monitor2Data.deleteMonitor2(dashboardId, item.id));
            });

            whereName(ctrl.newDashboardMonitor2es)
                .uniq(function (item) {
                    return item.name.toLowerCase();
                })
                .forEach(function (item) {
                    promises.push(monitor2Data.createMonitor2(dashboardId, item.name, item.url))
                });

            $q.all(promises)
                .then(function (responses) {
                    var widgetResponse = {
                        name: 'monitor2',
                        options: {
                            id: modalData.widgetConfig.options.id
                        }
                    };
                    $modalInstance.close(responses.length ? widgetResponse : null);
                })
        }
    }
})();