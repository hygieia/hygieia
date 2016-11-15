(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('deployViewController', deployViewController);

    deployViewController.$inject = ['$scope', 'DashStatus', 'deployData', 'DisplayState', '$q', '$modal'];
    function deployViewController($scope, DashStatus, deployData, DisplayState, $q, $modal) {
        /*jshint validthis:true */
        var ctrl = this;

        // public variables
        ctrl.environments = [];
        ctrl.statuses = DashStatus;

        ctrl.load = load;
        ctrl.showDetail = showDetail;

        function load() {
            var deferred = $q.defer();
            deployData.details($scope.widgetConfig.componentId).then(function(data) {
                processResponse(data.result);
                deferred.resolve(data.lastUpdated);
            });
            return deferred.promise;
        }

        function showDetail(environment) {
            $modal.open({
                controller: 'DeployDetailController',
                controllerAs: 'detail',
                templateUrl: 'components/widgets/deploy/detail.html',
                size: 'lg',
                resolve: {
                    environment: function() {
                        return environment;
                    },
                    collectorName: function () {
                        return $scope.dashboard.application.components[0].collectorItems.Deployment[0].collector.name;
                    },
                    collectorNiceName: function () {
                        return $scope.dashboard.application.components[0].collectorItems.Deployment[0].niceName;
                    }
                }
            });
        }

        function processResponse(data) {
            var worker = {
                getEnvironments: getEnvironments,
                getIsDefaultState: getIsDefaultState
            };

            function getIsDefaultState(data, cb) {
                var isDefaultState = true;
                _(data).forEach(function (environment) {
                    var offlineUnits = _(environment.units).where({'deployed': false}).value().length;

                    if(environment.units && environment.units.length == offlineUnits) {
                        isDefaultState = false;
                    }
                });

                cb(isDefaultState);
            }

            function getEnvironments(data, cb) {
                var environments = _(data).map(function (item) {

                    return {
                        name: item.name,
                        url: item.url,
                        units: item.units,
                        serverUpCount: getServerOnlineCount(item.units, true),
                        serverDownCount: getServerOnlineCount(item.units, false),
                        failedComponents: getFailedComponentCount(item.units),
                        lastUpdated: getLatestUpdate(item.units)
                    };

                    function getFailedComponentCount(units) {
                        return _(units).where({'deployed':false}).value().length;
                    }

                    function getServerOnlineCount(units, isOnline) {
                        var total = 0;
                        _(units).forEach(function (unit) {
                            total += _(unit.servers).where({'online':isOnline})
                                .value()
                                .length;
                        });

                        return total;
                    }

                    function getLatestUpdate(units) {
                        return _.max(units, function(unit) {
                            return unit.lastUpdated;
                        }).lastUpdated;
                    }
                }).value();

                cb({
                    environments: environments
                });
            }

            worker.getIsDefaultState(data, defaultStateCallback);
            worker.getEnvironments(data, environmentsCallback);
        }

        function defaultStateCallback(isDefaultState) {
            //$scope.$apply(function() {
                $scope.display = isDefaultState ? DisplayState.DEFAULT : DisplayState.ERROR;
            //});
        }

        function environmentsCallback(data) {
            //$scope.$apply(function () {
                ctrl.environments = data.environments;
            //});
        }
    }
})();
