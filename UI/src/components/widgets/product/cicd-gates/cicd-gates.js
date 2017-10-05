(function() {
  'use strict';

  angular
    .module(HygieiaConfig.module)
    .controller('CicdGatesController', CicdGatesController);

  CicdGatesController.$inject = ['$uibModalInstance', 'team', 'dashboardId', 'componentId'];

  function CicdGatesController($uibModalInstance, team, dashboardId, componentId) {
    /*jshint validthis:true */
    var ctrl = this;

    ctrl.teamname = team.customname || team.name;
    ctrl.collectorItemId = team.collectorItemId;
    ctrl.dashboardId = dashboardId;
    ctrl.componentId = componentId;
  }
})();
