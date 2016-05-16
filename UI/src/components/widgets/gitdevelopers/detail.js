(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('GitdeveloperDetailController', GitdeveloperDetailController);

    GitdeveloperDetailController.$inject = ['$modalInstance', 'gitdevelopers', 'DashStatus'];
    function GitdeveloperDetailController($modalInstance, gitdevelopers, DashStatus) {
        /*jshint validthis:true */
        var ctrl = this;

        ctrl.statuses = DashStatus;
        ctrl.gitdevelopers = gitdevelopers;

    }
})();
