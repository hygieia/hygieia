(function () {
   'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('awsStatusConfigController', awsStatusConfigController);

    awsStatusConfigController.$inject = ['$scope', '$q', '$modalInstance', 'awsStatusData', 'modalData'];
    function awsStatusConfigController($scope, $q, $modalInstance, awsStatusData, modalData) {
        var ctrl= this;

        // request and process data
        awsStatusData.details(modalData.dashboard.id)
            .then(processDetailResponse);
        awsStatusData.search()
            .then(processSearchResponse);

        // local variables
        var deletedDashboardStatuses = [];
        ctrl.appName = modalData.dashboard.application.name;
        ctrl.newDashboardAwsStatuses = [];
        ctrl.dashboardAwsStatuses = [];
        ctrl.allAwsStatuses = [];

        // methods
        ctrl.save = save;
        ctrl.deleteDashboardAwsStatus = deleteDashboardAwsStatus;
        ctrl.addNewDashboardAwsStatus = addNewDashboardAwsStatus;
        ctrl.deleteNewDashboardAwsStatus = deleteNewDashboardAwsStatus;

        // Processes the response of from the server for a detail request.
        function processDetailResponse(response) {
            var worker = {
                getAwsStatuses: getAwsStatuses
            };

            function getAwsStatuses(data, cb) {
                cb({dashboardAwsStatuses: getDashboardAwsStatuses(data.result.awsStatuses)})
            }

            function getDashboardAwsStatuses(awsStatuses) {
                return awsStatuses;
            }

            worker.getAwsStatuses(response, workerDetailCallback);
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
            ctrl.dashboardAwsStatuses = obj.dashboardAwsStatuses;
        }

        // Add deleted item to list for saving.
        function deleteDashboardAwsStatus(idx) {
            deletedDashboardStatuses.push(
                ctrl.dashboardAwsStatuses.splice(idx, 1)[0]
            );
        }

        // add new item to list for saving.
        function addNewDashboardAwsStatus() {
            ctrl.newDashboardAwsStatuses.push({name: '', url: ''});
        }

        // delete a status that hasnt been saved yet.
        function deleteNewDashboardAwsStatus(idx) {
            ctrl.newDashboardAwsStatuses.splice(idx, 1);
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
                promises.push(awsStatusData.deleteAwsStatus(dashboardId, item.id));
            });

            whereName(ctrl.newDashboardAwsStatuses)
                .uniq(function (item) {
                    return item.name.toLowerCase();
                })
                .forEach(function (item) {
                    promises.push(awsStatusData.createAwsStatus(dashboardId, item.name, item.url))
                });

            $q.all(promises)
                .then(function (responses) {
                    var widgetResponse = {
                        name: 'aws-status',
                        options: {
                            id: modalData.widgetConfig.options.id
                        }
                    };
                    $modalInstance.close(responses.length ? widgetResponse : null);
                })
        }
    }
})();