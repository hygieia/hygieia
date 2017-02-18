(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('RepoDetailController', RepoDetailController);

    RepoDetailController.$inject = ['$modalInstance', 'commits', 'pulls','issues','DashStatus'];
    function RepoDetailController($modalInstance, commits, pulls, issues, DashStatus) {
        /*jshint validthis:true */
        var ctrl = this;

        ctrl.statuses = DashStatus;
        ctrl.commits = commits;
        ctrl.pulls = pulls;
        ctrl.issues = issues;

    }
})();