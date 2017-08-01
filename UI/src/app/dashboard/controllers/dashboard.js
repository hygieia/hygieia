/**
 * Controller for the dashboard route.
 * Render proper template.
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('DashboardController', DashboardController);

    DashboardController.$inject = ['dashboard', '$location', 'dashboardService'];
    function DashboardController(dashboard, $location, dashboardService) {
        var ctrl = this;

        // if dashboard isn't available through resolve it may have been deleted
        // so redirect to the home screen
        if(!dashboard) {
            $location.path('/');
        }

        // set the template and make sure it has access to the dashboard objec
        // dashboard is guaranteed by the resolve setting in the route

        // public variables
        ctrl.templateUrl = 'components/templates/' + dashboard.template.toLowerCase() + '.html';
        dashboard.title = dashboardService.getDashboardTitle(dashboard);
        ctrl.dashboard = dashboard;

        console.log('Dashboard', dashboard);
    }
})();
