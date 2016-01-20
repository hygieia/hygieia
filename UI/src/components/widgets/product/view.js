(function () {
    'use strict';

    angular.module(HygieiaConfig.module)
        .directive('typeaheadFocus', function () {
            return {
                require: 'ngModel',
                link: function (scope, element, attr, ngModel) {

                    //trigger the popup on 'click' because 'focus'
                    //is also triggered after the item selection
                    element.bind('click', function () {

                        var viewValue = ngModel.$viewValue;

                        //restore to null value so that the typeahead can detect a change
                        if (ngModel.$viewValue == ' ') {
                            ngModel.$setViewValue(null);
                        }

                        //force trigger the popup
                        ngModel.$setViewValue(' ');

                        //set the actual value in case there was already a value in the input
                        ngModel.$setViewValue(viewValue || ' ');
                    });

                    //compare function that treats the empty space as a match
                    scope.emptyOrMatch = function (actual, expected) {
                        if (expected == ' ') {
                            return true;
                        }
                        return actual.indexOf(expected) > -1;
                    };
                }
            };
        });

    angular
        .module(HygieiaConfig.module)
        .controller('productViewController', productViewController);

    productViewController.$inject = ['$scope', '$modal', 'dashboardData', '$location', 'collectorData'];
    function productViewController($scope, $modal, dashboardData, $location, collectorData) {
        /*jshint validthis:true */
        var ctrl = this;

        // public properties

        // public methods
        ctrl.load = load;
        ctrl.editTeam = editTeam;
        ctrl.addTeam = addTeam;
        ctrl.openDashboard = openDashboard;

        function openDashboard(item) {
            collectorData.itemsByType('product').then(function(response) {
                _(response).forEach(function(board) {
                    if (item.collectorItemId == board.id) {
                        $location.path('/dashboard/' + board.options.dashboardId);
                    }
                });
            });
        }

        function load() {
            var options = $scope.widgetConfig.options;
            console.log($scope.dashboard);
            console.log(options);

            if (options && options.teams) {
                ctrl.configuredTeams = options.teams;
            }
        }

        function editTeam(team) {

            $modal.open({
                templateUrl: 'components/widgets/product/edit-team/edit-team.html',
                controller: 'editTeamController',
                controllerAs: 'ctrl',
                resolve: {
                    editTeamConfig: function() {
                        return {
                            team: team
                        }
                    }
                }
            }).result.then(function(config) {
                if(!config) {
                    return;
                }

                var options = $scope.widgetConfig.options;

                // take the collector item out of the team array
                if(config.remove) {
                    // do remove
                    var keepTeams = [];

                    _(options.teams).forEach(function(team) {
                        if(team.collectorItemId != config.collectorItemId) {
                            keepTeams.push(team);
                        }
                    });

                    options.teams = keepTeams;
                }
                else {
                    for(var x=0;x<options.teams.length;x++) {
                        if(options.teams[x].collectorItemId == config.collectorItemId) {
                            options.teams[x] = config;
                        }
                    }
                }

                updateWidgetOptions(options);
            });
        }

        function addTeam() {

            $modal.open({
                templateUrl: 'components/widgets/product/add-team/add-team.html',
                controller: 'addTeamController',
                controllerAs: 'ctrl'
            }).result.then(function(config) {
                if(!config) {
                    return;
                }

                // prepare our response for the widget upsert
                var options = $scope.widgetConfig.options;

                // make sure it's an array
                if(!options.teams || !options.teams.length) {
                    options.teams = [];
                }

                // add our new config to the array
                options.teams.push(config);

                updateWidgetOptions(options);
            });
        }

        function updateWidgetOptions(options)
        {
            // get a list of collector ids
            var collectorItemIds = [];
            _(options.teams).forEach(function(team) {
                collectorItemIds.push(team.collectorItemId);
            });

            var data = {
                name: 'product',
                componentId: $scope.dashboard.application.components[0].id,
                collectorItemIds: collectorItemIds,
                options: options
            };

            console.log('Upsert widget', data);
            $scope.upsertWidget(data);
        }
    }
})();
