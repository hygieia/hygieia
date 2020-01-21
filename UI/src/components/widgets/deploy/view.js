(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('deployViewController', deployViewController);

    deployViewController.$inject = ['$scope', 'DashStatus', 'deployData', 'DisplayState', '$q', '$uibModal'];
    function deployViewController($scope, DashStatus, deployData, DisplayState, $q, $uibModal) {
        /*jshint validthis:true */
        var ctrl = this;

        // public variables
        ctrl.environments = [];
        ctrl.statuses = DashStatus;
        ctrl.ignoreEnvironmentFailuresRegex=/^$/;

        if ($scope.widgetConfig.options.ignoreRegex !== undefined && $scope.widgetConfig.options.ignoreRegex !== null && $scope.widgetConfig.options.ignoreRegex !== '') {
            ctrl.ignoreEnvironmentFailuresRegex=new RegExp($scope.widgetConfig.options.ignoreRegex.replace(/^"(.*)"$/, '$1'));
        }

        ctrl.load = load;
        ctrl.showDetail = showDetail;
        ctrl.getChoice = getChoice;
        ctrl.getTotalListingCount = getTotalListingCount;
        ctrl.environmentsFilter = [];
        ctrl.minitabs = [
            {name: "Last 5 Deployments"},
            {name: "Last 10 Deployments"},
        ];

        ctrl.miniWidgetView = ctrl.minitabs[0].name;
        ctrl.miniToggleView = function (index) {
            ctrl.miniWidgetView = typeof ctrl.minitabs[index] === 'undefined' ? ctrl.minitabs[0].name : ctrl.minitabs[index].name;
        };

        function load() {
            var deferred = $q.defer();
            deployData.details($scope.widgetConfig.componentId).then(function(data) {
                processResponse(data.result);
                deferred.resolve(data.lastUpdated);
            });
            return deferred.promise;
        }

        function showDetail(environment) {
            $uibModal.open({
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

        function getTotalListingCount(){
            return ctrl.totalListingCount;
        }

        function getChoice(val){
            var size = -5;
            if(val !== "Last 5 Deployments"){
                size = -10;
            }
            ctrl.environmentsFilter = ctrl.environments.slice(size);
        }

        function processResponse(data) {
            var worker = {
                getEnvironments: getEnvironments,
                getIsDefaultState: getIsDefaultState
            };
            
            var ignoreEnvironmentFailuresRegex = ctrl.ignoreEnvironmentFailuresRegex;
            
            function ignoreEnvironmentFailures(environment) {
            	return ignoreEnvironmentFailuresRegex.test(environment.name);
            }

            function getIsDefaultState(data, cb) {
                var isDefaultState = true;
                _(data).forEach(function (environment) {
                    var offlineUnits = _(environment.units).filter({'deployed': false}).value().length;

                    if(environment.units && environment.units.length == offlineUnits
                    		&& !ignoreEnvironmentFailures(environment)) {
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
                        ignoreFailure: ignoreEnvironmentFailures(item),
                        lastUpdated: getLatestUpdate(item.units)
                    };

                    function getFailedComponentCount(units) {
                        return _(units).filter({'deployed':false}).value().length;
                    }

                    function getServerOnlineCount(units, isOnline) {
                        var total = 0;
                        _(units).forEach(function (unit) {
                            total += _(unit.servers).filter({'online':isOnline})
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
            ctrl.totalListingCount = data.environments.length;
            ctrl.environments = data.environments;
            ctrl.environmentsFilter = data.environments.slice(-5);
        }
    }
})();
