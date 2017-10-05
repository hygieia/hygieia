(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('scoreBoardDetailsController', scoreBoardDetailsController);

    scoreBoardDetailsController.$inject = ['$scope', '$uibModalInstance', 'productViewController', 'scoreBoardDetailsConfig'];
    function scoreBoardDetailsController($scope, $uibModalInstance, productViewController, scoreBoardDetailsConfig) {
        /*jshint validthis:true */
        var ctrl = this;

        ctrl.metricName = scoreBoardDetailsConfig.metricName;
        ctrl.teamName = scoreBoardDetailsConfig.teamName;
        ctrl.score = scoreBoardDetailsConfig.metricScore;
        ctrl.value = scoreBoardDetailsConfig.metricValue;

    }
})();
