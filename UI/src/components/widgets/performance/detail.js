(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('PerformanceDetailController', PerformanceDetailController);

    PerformanceDetailController.$inject = ['$uibModalInstance','index', 'warnings', 'good', 'bad', 'DashStatus'];
    function PerformanceDetailController($uibModalInstance, index, warnings, good, bad, DashStatus) {
        /*jshint validthis:true */
        var ctrl = this;

        console.log(index);

        if (index == 0){
          ctrl.healthruleviolations = good.reverse();
        }else if (index == 1){
          ctrl.healthruleviolations = warnings.reverse();
        }else{
          ctrl.healthruleviolations = bad.reverse();
        }

    }
})();
