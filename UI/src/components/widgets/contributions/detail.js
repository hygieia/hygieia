(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('RepoDetailController', RepoDetailController);

    RepoDetailController.$inject = ['$modalInstance', 'commits', 'DashStatus'];
    function RepoDetailController($modalInstance, commits, DashStatus) {
        /*jshint validthis:true */
        var ctrl = this;

        ctrl.statuses = DashStatus;
        ctrl.commits = commits;

    }
})();