/**
 * Controller for the modal popup when creating
 * a new dashboard on the startup page
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('RenameDashboardController', RenameDashboardController);

    RenameDashboardController.$inject = ['$uibModalInstance', 'dashboardData', 'dashboardId','dashboardName'];
    function RenameDashboardController($uibModalInstance, dashboardData, dashboardId,dashboardName) {

        var ctrl = this;

        // public variables
        ctrl.dashboardTitle = dashboardName;

        // public methods
        ctrl.submit = submit;


        function submit(form) {

            form.dashboardTitle.$setValidity('renameError', true);

            if (form.$valid) {
                dashboardData
                    .rename(dashboardId, document.cdf.dashboardTitle.value)
                    .success(function (data) {
                        $uibModalInstance.close();
                        window.location.reload(false);
                })
                    .error(function(data){
                    form.dashboardTitle.$setValidity('renameError', false);
                });
            }
            else
            {
                form.dashboardTitle.$setValidity('renameError', false);
            }

        }

    }
})();
