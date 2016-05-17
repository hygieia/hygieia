(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('PullMergedDetailController', PullMergedDetailController);

    PullMergedDetailController.$inject = ['$modalInstance', 'pulls', 'DashStatus'];
    function PullMergedDetailController($modalInstance, pulls, DashStatus) {
        /*jshint validthis:true */
        var ctrl = this;

        ctrl.statuses = DashStatus;
        ctrl.pulls = pulls;

    }
})();
