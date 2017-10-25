/**
 * Controller for delete widget.
 *
 */
(function () {
    'use strict';
    angular
        .module(HygieiaConfig.module)
        .controller('DeleteWidgetTemplateController', DeleteWidgetTemplateController)
        .directive('ngIncludeTemplate', function() {
            return {
                templateUrl: function(elem, attrs) {
                    return attrs.ngIncludeTemplate;
                },
                restrict: 'A',
                scope: {
                    'ngIncludeVariables': '&'
                },
                link: function(scope, elem, attrs) {
                    var vars = scope.ngIncludeVariables();
                    Object.keys(vars).forEach(function(key) {
                        scope[key] = vars[key];
                    });
                }
            }
        });

    DeleteWidgetTemplateController.$inject = ['$scope','dashboardData'];
    function DeleteWidgetTemplateController($scope,dashboardData) {
        var ctrl = this;
        ctrl.removeConfig = removeConfig;

        function removeConfig(){
            var widget = $scope.widget;
            var dashboardId = $scope.dashboardId;
             var widgetConfig =  $scope.widgetConfig;
           dashboardData.deleteWidget(dashboardId,widget).success(function (response) {
                window.location.reload(true);
            }).error(function () {
               console.log("Error deleting widget");
            });
        }

    }
})();



