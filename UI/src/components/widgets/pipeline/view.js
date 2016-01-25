(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('pipelineViewController', pipelineViewController);

    pipelineViewController.$inject = ['$scope', 'deployData', 'WidgetState', '$q'];
    function pipelineViewController($scope, deployData, WidgetState, $q) {
        /*jshint validthis:true */
        var ctrl = this;

        // placeholder for environments that are not deployed or have a server down
        var currentDownEnvironments = [];

        // list of valid environments to validate and build data
        var validMappings = ['dev', 'qa', 'int', 'perf', 'prod'];

        ctrl.load = function() {
            // verify that a valid mapping exists
            var configLength = (function(map) {
                var length = 0;
                for(var key in map) {
                    if(validMappings.indexOf(key) != -1) {
                        length++;
                    }
                }
                return length;
            })($scope.widgetConfig.options.mappings);

            // if no valid mapping exists go back to configuration state
            if(configLength === 0) {
                $scope.widgetConfig.options.mappings = {};
                $scope.setState(WidgetState.CONFIGURE);
            } else {
                var deferred = $q.defer();
                deployData.details($scope.dashboard.application.components[0].id).then(function(data) {
                    processResponse(data.result);
                    deferred.resolve(data.lastUpdated);
                });

                return deferred.promise;
            }
        };

        // a list of environments used to loop environments in the view
        ctrl.environmentKeys = [];

        // build up the environment keys array
        _(validMappings).forEach(function (key) {
            if($scope.widgetConfig.options.mappings[key]) {
                ctrl.environmentKeys.push(key);
            }
        });

        // a grid width class to use based on the number of environments displayed.
        // values are captured by index of the displayed environment length
        var gridSizes = [12, 12, 6, 4, 3, 'fifths', 2];
        ctrl.colGridSize = gridSizes[ctrl.environmentKeys.length];

        // method to determine if environment is down and should display red marking
        ctrl.isDown = isDown;

        function processResponse(data) {
            var hasUnit = false;
            var mappings = $scope.widgetConfig.options.mappings;
            var units = {};
            var downEnvironments = [];

            // loop through the list of environments we're going to display. starting here
            // will ensure the same data in two columns if it's configured that way
            _(ctrl.environmentKeys).forEach(function (envKey) {
                // limit our data to environments in our mappings file
                var environments =
                _(data).where(function(env) {
                    return mappings[envKey] && mappings[envKey].toLowerCase() == env.name.toLowerCase();
                })
                    .forEach(function (env) {

                        // look at each unit and add data for the current environment key
                        _(env.units).forEach(function (unit) {
                            var unitValue = unit.name.toLowerCase();

                            // if this unit is not already in the area go ahead and add a placeholder object
                            if(!units[unitValue]) {
                                var defaultEnvironments = {};
                                _(ctrl.environmentKeys).forEach(function(value) {
                                    defaultEnvironments[value] = {version:'',lastUpdate:''};
                                });

                                units[unitValue] = {
                                    name: unit.name,
                                    environments: defaultEnvironments
                                };
                            }

                            // if it wasn't deployed or one of the servers is down the environment is considered down
                            var somethingDown = !unit.deployed;
                            if(!somethingDown) {
                                somethingDown = _(unit.servers).where(function (server) {
                                    return !server.online;
                                }).value().length > 0;
                            }

                            // add the down environment to the arra
                            if(somethingDown && downEnvironments.indexOf(envKey) == -1) {
                                downEnvironments.push(envKey);
                            }

                            // populate the unit data for this environment
                            hasUnit = true;
                            units[unitValue].environments[envKey] = {
                                version: unit.version,
                                lastUpdate: unit.lastUpdated,
                                somethingDown: somethingDown
                            };
                        });
                    });
            });

            // set angular data
            if(hasUnit) {
                currentDownEnvironments = downEnvironments;
                ctrl.units = units;
            } else {
                // may have been configured for another app so set to config mode
                $scope.setState(WidgetState.CONFIGURE);
            }
        }

        // checks the environment against the list of down environments
        function isDown(key) {
            return currentDownEnvironments.indexOf(key) != -1;
        }
    }
})();
