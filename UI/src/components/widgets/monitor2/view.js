(function () {
    'use strict';
    angular
        .module(HygieiaConfig.module)
        .controller('monitor2ViewController', monitor2ViewController)
        .controller('monitor2StatusController', monitor2StatusController);
    
    monitor2ViewController.$inject = ['$scope', 'monitor2Data', 'DashStatus', '$modal', '$q', '$http'];
    function monitor2ViewController($scope, monitor2Data, DashStatus, $modal, $q, $http) {
        var ctrl = this;
        
        ctrl.statuses = DashStatus; // The status icons for if the server is up/down
        ctrl.monitor2es = [];
        
        // public methods
        ctrl.openStatusWindow = openStatusWindow;
        
        ctrl.load = function () {
            var deferred = $q.defer();
            monitor2Data.details($scope.dashboard.id)
                .then(function (data) {
                    processResponse(data.result);
                    deferred.resolve(data.lastUpdated)
                });
            return deferred.promise;
        };
        
        function openStatusWindow(monitor2) {
            $modal.open({
                templateUrl: 'monitor2Status.html',
                controller: 'monitor2StatusController',
                controllerAs: 'ctrl',
                scope: $scope,
                size: 'md',
                resolve: {
                    statuses: function () {
                        return DashStatus;
                    },
                    monitor2: function  () {
                        return {
                            id: monitor2.id,
                            name: monitor2.name,
                            url: monitor2.url
                        };
                    }
                }
            }).result
                .then(function (updatedMonitor2) {
                    if (!updatedMonitor2) {
                        return;
                    }
                    _(ctrl.monitor2es).forEach(function (monitor2, idx) {
                        if (monitor2.id == updatedMonitor2.id) {
                            ctrl.monitor2es[idx] = angular.extend(monitor2, updatedMonitor2);
                        }
                    });
                    
                    monitor2Data.updateMonitor2($scope.dashboard.id, updatedMonitor2);
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
                monitor2es: get(data.monitor2es, false)
            });

            function get(monitor2es) {
                var defer = $q.defer();
                var promises = [];
                angular.forEach(monitor2es, function (monitor2) {
                    promises.push(monitor2Data
                        .getMonitor2Status($scope.dashboard.id,monitor2.id,{name:monitor2.name, url:monitor2.url, 
                            status:monitor2.status}));
                });

                return $q.all(promises);
            }
        }

        function workerCallback(data) {
            data.monitor2es.then(function(result) {
                ctrl.monitor2es = result;
            });
        }
    }

    monitor2StatusController.$inject = ['monitor2', 'statuses', '$modalInstance'];
    function monitor2StatusController(monitor2, statuses, $modalInstance) {
        var ctrl = this;

        ctrl.monitor2 = monitor2;
        ctrl.statuses = statuses;
        ctrl.getStatus = getStatus;
        ctrl.submit = submit;

        function getStatus() {
            // not used?
        }

        function submit() {
            $modalInstance.close(ctrl.monitor2);
        }
    }
})();