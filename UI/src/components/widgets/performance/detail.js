(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('PerformanceDetailController', PerformanceDetailController);

    PerformanceDetailController.$inject = ['$modalInstance', '$http', 'deeplink', 'calls', 'calllabels', 'errorlabels', 'errors', 'appid', 'DashStatus'];
    function PerformanceDetailController($modalInstance, $http, deeplink, calls, calllabels, errorlabels, errors, appid, DashStatus) {
        /*jshint validthis:true */
        var ctrl = this;

        console.log(deeplink);
        ctrl.statuses = DashStatus;
        ctrl.deeplink = deeplink;
        ctrl.calls = calls;
        ctrl.errors = errors;
        ctrl.appID = appid;
        ctrl.calllabels = calllabels;
        ctrl.errorlabels = errorlabels;

      
    }
})();
