(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('RepoDetailController', RepoDetailController);

    RepoDetailController.$inject = ['$uibModalInstance', 'commits', 'pulls','issues','DashStatus'];
    function RepoDetailController($uibModalInstance, commits, pulls, issues, DashStatus) {
        /*jshint validthis:true */
        var ctrl = this;

        ctrl.statuses = DashStatus;
        ctrl.commits = commits;
        ctrl.pulls = pulls;
        ctrl.issues = issues;

    }
})();