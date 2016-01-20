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
                templateUrl: 'components/widgets/product/edit-team.html',
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

                // prepare our response for the widget upsert
                var collectorItemIds = [],
                    component = $scope.dashboard.application.components[0],
                    forCollectorItemId = config.forCollectorItemId;

                // add to our existing collector ids if they exist
                if(component.collectorItems && component.collectorItems.Product)
                {
                    for(var item in component.collectorItems.Product) {
                        // make sure item exists and isn't the collector we are editing
                        if(item.collectorId && (!forCollectorItemId || forCollectorItemId != item.collectorId))
                        {
                            collectorItemIds.push(item.collectorId);
                        }
                    }
                }

                // add the dashboard we chose to the collector item id list
                collectorItemIds.push(config.collectorItemId);

                var options = $scope.widgetConfig.options;

                if(!options.teams || !options.teams.length) {
                    options.teams = [];
                }

                // try to update an existing dashbaord
                var found = false;

                delete config.forCollectorItemId;

                for(var x=0; x<options.teams.length;x++) {
                    if(options.teams[x].collectorItemId == forCollectorItemId) {
                        found = true;
                        options.teams[x] = config;
                    }
                }

                // if it didn't already exist it was new so just add it
                if(!found) {
                    options.teams.push(config);
                }

                var data = {
                    name: 'product',
                    componentId: component.id,
                    collectorItemIds: collectorItemIds,
                    options: options
                };

                console.log('Upsert widget', data);
                $scope.upsertWidget(data);
            });
        }
    }
})();
