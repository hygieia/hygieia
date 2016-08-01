(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('PerformanceDetailController', PerformanceDetailController);

    PerformanceDetailController.$inject = ['$modalInstance', '$http', 'index', 'healthruleviolations', 'calls', 'calllabels', 'errorlabels', 'errors', 'appid', 'DashStatus'];
    function PerformanceDetailController($modalInstance, $http, index, healthruleviolations, calls, calllabels, errorlabels, errors, appid, DashStatus) {
        /*jshint validthis:true */
        var ctrl = this;

        console.log(index);
        ctrl.statuses = DashStatus;
        ctrl.index = index;
        ctrl.calls = calls;
        ctrl.errors = errors;
        ctrl.appID = appid;
        ctrl.calllabels = calllabels;
        ctrl.errorlabels = errorlabels;
        ctrl.healthruleviolations = healthruleviolations.value.splice(0,14);

        console.log(ctrl.healthruleviolations);
        console.log(typeof healthruleviolations);

        var temp = [];
        ctrl.violationlength = healthruleviolations.value.length;
        console.log(ctrl.violationlength);


    }
})();
