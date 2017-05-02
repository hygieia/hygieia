(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('monitorViewController', monitorViewController)
        .controller('monitorStatusController', monitorStatusController);

    monitorViewController.$inject = ['$scope', 'monitorData', 'DashStatus', '$uibModal', '$q', '$interval'];
    function monitorViewController($scope, monitorData, DashStatus, $uibModal, $q, $interval) {
        /*jshint validthis:true */
        var ctrl = this;

        // public variables
        ctrl.statuses = DashStatus;
        ctrl.services = [];
        ctrl.dependencies = [];

        // public methods
        ctrl.openStatusWindow = openStatusWindow;
        ctrl.hasMessage = hasMessage;

        ctrl.load = function() {
            // grab data from the api
            var deferred = $q.defer();
            monitorData.details($scope.dashboard.id).then(function(data) {
                processResponse(data.result);
                deferred.resolve(data.lastUpdated);
            });
            return deferred.promise;
        };

        $interval(function () {
           ctrl.load();
            for (var i = 0; i < ctrl.services.length; i++) {
                monitorData.refreshService($scope.dashboard.id, ctrl.services[i]);
              }

        }, 60000);



        // method implementations
        function hasMessage(service) {
            return service.message && service.message.length;
        }

        function openStatusWindow(service) {
            // open up a new modal window for the user to set the status
            $uibModal.open({
                templateUrl: 'monitorStatus.html',
                controller: 'monitorStatusController',
                controllerAs: 'ctrl',
                scope: $scope,
                size: 'md',
                resolve: {
                    // make sure modal has access to the status and selected
                    statuses: function () {
                        return DashStatus;
                    },
                    service: function () {
                        return {
                            id: service.id,
                            name: service.name,
                            status: service.status,
                            url: service.url,
                            message: service.message
                        };
                    }
                }
            }).result
                .then(function (updatedService) {
                    // if the window is closed without saving updatedService will be null
                    if(!updatedService) {
                        return;
                    }

                    // update locally
                    _(ctrl.services).forEach(function(service, idx) {
                        if(service.id == updatedService.id) {
                            ctrl.services[idx] = angular.extend(service, updatedService);
                        }
                    });

                    // update the api
                    monitorData.updateService($scope.dashboard.id, updatedService);
                });
        }

        ctrl.showIconLegend = function() {
        	$uibModal.open({
        		templateUrl: 'components/widgets/monitor/icon-legend.html'
        	})
        }
        
        function processResponse(response) {
            var worker = {
                    doWork: workerDoWork
                };

            worker.doWork(response, DashStatus, workerCallback);
        }

        function workerDoWork(data, statuses, cb) {
            cb({
                services: get(data.services, false),
                dependencies: get(data.dependencies, true)
            });

            function get(services, dependency) {
                return _.map(services, function (item) {
                    var name = item.name;

                    if (dependency && item.applicationName) {
                        name = item.applicationName + ': ' + name;
                    }

                    if(item.status && (typeof item.status == 'string' || item.status instanceof String)) {
                        item.status = item.status.toLowerCase();
                    }

                    switch (item.status) {
                        case 'ok':
                            item.status = statuses.PASS;
                            break;
                        case 'warning':
                            item.status = statuses.WARN;
                            break;
                        case 'unauth':
                        	item.status = statuses.UNAUTH;
                        	break;
                        case 'alert':
                            item.status = statuses.FAIL;
                            break;
                    }

                    return {
                        id: item.id,
                        name: name,
                        url: item.url,
                        status: item.status,
                        message: item.message
                    };
                });
            }
        }

        function workerCallback(data) {
            //$scope.$apply(function () {
                ctrl.services = data.services;
                ctrl.dependencies = data.dependencies;
            //});
        }
    }

    monitorStatusController.$inject = ['service', 'statuses', '$uibModalInstance'];
    function monitorStatusController(service, statuses, $uibModalInstance) {
        /*jshint validthis:true */
        var ctrl = this;

        // public variables
        ctrl.service = service;
        ctrl.statuses = statuses;
        ctrl.setStatus = setStatus;

        // public methods
        ctrl.submit = submit;

        function setStatus(status) {
            ctrl.service.status = status;
        }

        function submit() {
            // pass the service back so the widget can update
            $uibModalInstance.close(ctrl.service);
        }
    }
})();
