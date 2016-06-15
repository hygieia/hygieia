(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('monitorConfigController', monitorConfigController);

    monitorConfigController.$inject = ['$scope', '$q', '$modalInstance', 'monitorData', 'modalData'];
    function monitorConfigController($scope, $q, $modalInstance, monitorData, modalData) {
        /*jshint validthis:true */
        var ctrl = this;

        // request our data
        monitorData.details(modalData.dashboard.id).then(processDetailResponse);
        monitorData.search().then(processSearchResponse);

        // local variables
        var deletedDashboardServices = [];
        var deletedDependentServices = [];

        // variables
        ctrl.appName = modalData.dashboard.application.name;
        ctrl.newDashboardServices = [];
        ctrl.newDependentServices = [];

        // set by api response worker
        ctrl.dashboardServices = [];
        ctrl.dependentServices = [];
        ctrl.allServices = [];

        // methods
        ctrl.save = save;

        ctrl.deleteDashboardService = deleteDashboardService;
        ctrl.addNewDashboardService = addNewDashboardService;
        ctrl.deleteNewDashboardService = deleteNewDashboardService;

        ctrl.deleteDependentService = deleteDependentService;
        ctrl.addNewDependentService = addNewDependentService;
        ctrl.deleteNewDependentService = deleteNewDependentService;


        function processDetailResponse(response) {
            var worker = {
                getServices: getServices
            };

            function getServices(data, cb) {
                cb({
                    dashboardServices: getDashboardServices(data.result.services),
                    dependentServices: getDependentServices(data.result.dependencies)
                });

                function getDashboardServices(services) {
                    return services;
                }

                function getDependentServices(services) {
                    if (services) {
                        for(var x=0;x<services.length;x++) {
                            var item = services[x];

                            services[x].name = item.applicationName + ': ' + item.name;
                        }
                    }

                    return services;
                }
            }

            worker.getServices(response, workerDetailCallback);
        }

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
            //$scope.$apply(function () {
                ctrl.dashboardServices = obj.dashboardServices;
                ctrl.dependentServices = obj.dependentServices;
            //});
        }

        function deleteDashboardService(idx) {
            deletedDashboardServices.push(
                ctrl.dashboardServices.splice(idx, 1)[0]
            );
        }

        function addNewDashboardService() {
            ctrl.newDashboardServices.push({name: ''});
        }

        function deleteNewDashboardService(idx) {
            ctrl.newDashboardServices.splice(idx, 1);
        }

        function deleteDependentService(idx) {
            deletedDependentServices.push(
                ctrl.dependentServices.splice(idx, 1)[0]
            );
        }

        function addNewDependentService() {
            ctrl.newDependentServices.push({selectedItem: {}});
        }

        function deleteNewDependentService(idx) {
            ctrl.newDependentServices.splice(idx, 1);
        }


        function save() {
            var dashboardId = modalData.dashboard.id;
            var promises = [];

            function whereName(data) {
                return _(data).where(function (item) {
                    return item.name && item.name.length;
                });
            }

            _(deletedDashboardServices).forEach(function (item) {
                promises.push(monitorData.deleteService(dashboardId, item.id));
            });

            _(deletedDependentServices).forEach(function (item) {
                promises.push(monitorData.deleteDependentService(dashboardId, item.id));
            });

            whereName(ctrl.newDashboardServices)
                .uniq(function (item) {
                    return item.name.toLowerCase();
                })
                .forEach(function (item) {
                    promises.push(monitorData.createService(dashboardId, item.name));
                });

            whereName(_.map(ctrl.newDependentServices, function (item) {
                return item.selectedItem;
            }))
                .forEach(function (item) {
                    promises.push(monitorData.createDependentService(dashboardId, item.id));
                });

            $q.all(promises).then(function (responses) {
                var widgetResponse = {
                    name: 'monitor',
                    options: {
                        id: modalData.widgetConfig.options.id
                    }
                };
                $modalInstance.close(responses.length ? widgetResponse : null);
            });
        }
    }
})();
