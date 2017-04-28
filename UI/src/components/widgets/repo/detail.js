(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('RepoDetailController', RepoDetailController);

    RepoDetailController.$inject = ['$uibModalInstance', 'commits', 'DashStatus'];
    function RepoDetailController($uibModalInstance, commits, DashStatus) {
        /*jshint validthis:true */
        var ctrl = this;

        ctrl.statuses = DashStatus;
        ctrl.commits = commits;

    }
})();