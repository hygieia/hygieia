(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('LevelonemanagerDetailController', LevelonemanagerDetailController);

    LevelonemanagerDetailController.$inject = ['$modalInstance', 'levelonemanagers', 'DashStatus'];
    function LevelonemanagerDetailController($modalInstance, levelonemanagers, DashStatus) {
        /*jshint validthis:true */
        var ctrl = this;

        ctrl.statuses = DashStatus;
        ctrl.levelonemanagers = levelonemanagers;

    }
})();
