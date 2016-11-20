/**
 * Controller for the modal popup when creating
 * a new dashboard on the startup page
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('RenameDashboardController', RenameDashboardController);

    RenameDashboardController.$inject = ['$location', '$modalInstance', 'dashboardData', '$cookies', 'DashboardType', 'dashboardId','dashboardName'];
    function RenameDashboardController($location, $modalInstance, dashboardData, $cookies, DashboardType, dashboardId,dashboardName) {

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
                $modalInstance.dismiss();

                var page = $location.path();

                if (page == "/admin") {
                    $location.path('/admin/');
                }
                else {
                    $location.path('/site/');
                }

            }

        }
        
    }
})();