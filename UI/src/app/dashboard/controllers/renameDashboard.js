/**
 * Controller for the modal popup when creating
 * a new dashboard on the startup page
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('RenameDashboardController', RenameDashboardController);

    RenameDashboardController.$inject = ['$location', '$modalInstance', 'dashboardData', 'dashboardId','dashboardName','$route'];
    function RenameDashboardController($location, $modalInstance, dashboardData, dashboardId,dashboardName,$route) {

        var ctrl = this;

        // public variables
        ctrl.dashboardTitle = dashboardName;

        // public methods
        ctrl.submit = submit;


        function submit(form) {

            form.dashboardTitle.$setValidity('createError', true);

            if (form.$valid) {
                dashboardData.rename(dashboardId, document.cdf.dashboardTitle.value);
                // close dialog
                $modalInstance.close();
            }
            else
            {
                form.dashboardTitle.$setValidity('creatError', false);
            }

        }
        
    }
})();