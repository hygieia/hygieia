(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('LeveltwomanagerDetailController', LeveltwomanagerDetailController);

    LeveltwomanagerDetailController.$inject = ['$modalInstance', 'leveltwomanagers', 'DashStatus'];
    function LeveltwomanagerDetailController($modalInstance, leveltwomanagers, DashStatus) {
        /*jshint validthis:true */
        var ctrl = this;

        ctrl.statuses = DashStatus;
        ctrl.leveltwomanagers = leveltwomanagers;

    }
})();
