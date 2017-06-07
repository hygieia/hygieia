/**
 * Standard status icon for various widgets
 */
(function() {
  'use strict';

  angular
    .module(HygieiaConfig.module + '.core')
    .directive('cicdGatesModal', cicdGatesModal);

  cicdGatesModal.$inject = ['cicdGatesData'];

  function cicdGatesModal(cicdGatesData) {
    return {
      scope: {
        name: '=',
        dashboardId: '=',
        collectorItemId: '=',
        componentId: '='
      },
      restrict: 'EA',
      controller: controller,
      templateUrl: 'app/dashboard/views/gates-block.html'
    };

    function controller($scope, cicdGatesData) {
      $scope.data = {};
      $scope.init = function() {
        cicdGatesData.details($scope.name, $scope.dashboardId, $scope.collectorItemId, $scope.componentId).then(function(response) {
          $scope.data = response;
        });
      };
      $scope.init();
    }
  }
})();
