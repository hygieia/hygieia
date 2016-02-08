angular
    .module(HygieiaConfig.module + '.core')
    .directive('commitBlocks', function () {
        return {
            restrict: 'E',
            scope: {
                fails: '=commitFail',
                passes: '=commitPass'
            },
            template: '<div class="town-city">'
                + '<div class="commit-fail state" ng-repeat="n in range(1,stateFail)"></div>'
                + '<div class="commit-pass state" ng-repeat="n in range(1,statePass)"></div>'
                + '<div class="commit-fail city" ng-repeat="n in range(1,cityFail)"></div>'
                + '<div class="commit-pass city" ng-repeat="n in range(1,cityPass)"></div>'
                + '<div class="commit-fail town" ng-repeat="n in range(1,townFail)"></div>'
                + '<div class="commit-pass town" ng-repeat="n in range(1,townPass)"></div>'
                + '<div class="commit-fail village" ng-repeat="n in range(1,villageFail)"></div>'
                + '<div class="commit-pass village" ng-repeat="n in range(1,villagePass)"></div>'
                + '</div>',
            controller: function($scope) {
                $scope.range = function(min, max, step) {
                    step = step || 1;
                    var input = [];
                    for (var i = min; i <= max; i += step) {
                        input.push(i);
                    }
                    return input;
                };

                function updateScopeValues() {
                    var pass = $scope.passes || 0,
                        fail = $scope.fails || 0;

                    $scope.stateFail = Math.floor(fail / 1000);
                    $scope.statePass = Math.floor(pass / 1000);

                    fail %= 1000;
                    pass %= 1000;

                    $scope.cityFail = Math.floor(fail / 100);
                    $scope.cityPass = Math.floor(pass / 100);

                    fail %= 100;
                    pass %= 100;

                    $scope.townFail = Math.floor(fail / 10);
                    $scope.townPass = Math.floor(pass / 10);

                    fail %= 10;
                    pass %= 10;

                    $scope.villageFail = fail;
                    $scope.villagePass = pass;
                }

                $scope.$watch("passes",function(newValue,oldValue) {
                    //This gets called when data changes.
                    updateScopeValues();
                });
                $scope.$watch("fails",function(newValue,oldValue) {
                    //This gets called when data changes.
                    updateScopeValues();
                });

                updateScopeValues();

            }
        };
    });