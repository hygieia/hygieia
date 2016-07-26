(function () {
    'use strict';
    angular
        .module(HygieiaConfig.module)
        .controller('awsStatusViewController', awsStatusViewController)
        .controller('awsStatusStatusController', awsStatusStatusController);
    
    awsStatusViewController.$inject = ['$scope', 'awsStatusData', 'DashStatus', '$modal', '$q', '$http'];
    function awsStatusViewController($scope, awsStatusData, DashStatus, $modal, $q, $http) {
        var ctrl = this;
        
        ctrl.statuses = DashStatus; // The status icons for if the server is up/down
        ctrl.awsStatuses = [];
        
        // public methods
        ctrl.openStatusWindow = openStatusWindow;
        
        ctrl.load = function () {
            var deferred = $q.defer();
            awsStatusData.details($scope.dashboard.id)
                .then(function (data) {
                    processResponse(data.result);
                    deferred.resolve(data.lastUpdated)
                });
            return deferred.promise;
        };
        
        function openStatusWindow(awsStatus) {
            $modal.open({
                templateUrl: 'awsStatusStatus.html',
                controller: 'awsStatusStatusController',
                controllerAs: 'ctrl',
                scope: $scope,
                size: 'md',
                resolve: {
                    statuses: function () {
                        return DashStatus;
                    },
                    awsStatus: function  () {
                        return {
                            id: awsStatus.id,
                            name: awsStatus.name,
                            url: awsStatus.url
                        };
                    }
                }
            }).result
                .then(function (updatedAwsStatus) {
                    if (!updatedAwsStatus) {
                        return;
                    }
                    _(ctrl.awsStatuses).forEach(function (awsStatus, idx) {
                        if (awsStatus.id == updatedAwsStatus.id) {
                            ctrl.awsStatuses[idx] = angular.extend(awsStatus, updatedAwsStatus);
                        }
                    });
                    
                    awsStatusData.updateAwsStatus($scope.dashboard.id, updatedAwsStatus);
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
                awsStatuses: get(data.awsStatuses, false)
            });

            function get(awsStatuses) {
                return _.map(awsStatuses, function(item) {
                    console.log('getting url...');
                    $http.head(item.url)
                        .success(function (response) {
                            console.log('PASS');
                            item.status = statuses.PASS;
                        })
                        .error(function (response) {
                            console.log(response.status);
                            item.status = statuses.FAIL;
                        });
                        // .then(function (response) {
                        //     console.log('Status ' + response.status);
                        //     switch (response.status) {
                        //         case 200:
                        //             item.status = statuses.PASS;
                        //             break;
                        //         case 404:
                        //             item.status = statuses.FAIL;
                        //             break;
                        //     }
                        // }, function (response) {
                        //     item.status = statuses.FAIL;
                        // });
                    return {
                        id: item.id,
                        name: item.name,
                        url: item.url,
                        status: item.status
                    }
                });
            }
        }

        function workerCallback(data) {
            ctrl.awsStatuses = data.awsStatuses;
        }
    }

    awsStatusStatusController.$inject = ['awsStatus', 'statuses', '$modalInstance'];
    function awsStatusStatusController(awsStatus, statuses, $modalInstance) {
        var ctrl = this;

        ctrl.awsStatus = awsStatus;
        ctrl.statuses = statuses;
        ctrl.getStatus = getStatus;
        ctrl.submit = submit;

        function getStatus() {
            // not used?
        }

        function submit() {
            $modalInstance.close(ctrl.awsStatus);
        }
    }
})();