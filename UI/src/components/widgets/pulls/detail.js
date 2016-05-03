(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('PullDetailController', PullDetailController);

    PullDetailController.$inject = ['$modalInstance', 'pulls', 'DashStatus'];
    function PullDetailController($modalInstance, pulls, DashStatus) {
        /*jshint validthis:true */
        var ctrl = this;

        ctrl.statuses = DashStatus;
        ctrl.pulls = pulls;

    }
})();
