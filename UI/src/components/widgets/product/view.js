(function () {
    'use strict';

    angular.module('devops-dashboard')
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
        .module('devops-dashboard')
        .controller('productViewController', productViewController);

    productViewController.$inject = ['$scope', '$modal', 'dashboardData'];
    function productViewController($scope, $modal, dashboardData) {
        /*jshint validthis:true */
        var ctrl = this;

        // public properties

        // public methods
        ctrl.load = load;
        ctrl.editTeam = editTeam;

        function load() {
            var options = $scope.widgetConfig.options;

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

                // add the new config to the teams list
                var options = $scope.widgetConfig.options;
                if(!options.teams || !options.teams.length) {
                    options.teams = [];
                }
                options.teams.push(config);

                // call the parent and save the widget
                $scope.upsertWidget({
                    name: 'product',
                    options: options
                });
            });
        }
    }
})();
