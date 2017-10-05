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
        if (!dashboard) {
            $location.path('/');
        }

        // set the template and make sure it has access to the dashboard objec
        // dashboard is guaranteed by the resolve setting in the route

        // public variables
        var dashboardTemplate = dashboard.template.toLowerCase();
        if (dashboardTemplate == 'capone' || dashboardTemplate == 'product-dashboard' || dashboardTemplate == 'caponechatops' || dashboardTemplate == 'cloud' ||
            dashboardTemplate == 'splitview') {
            ctrl.templateUrl = 'components/templates/' + dashboardTemplate + '.html';
        }
        else if(dashboardTemplate == 'widgets') {
            ctrl.templateUrl = 'components/templates/widgetsTemplate.html';
        } else {
                ctrl.templateUrl = 'components/templates/customTemplate.html';
            }
            dashboard.title = dashboardService.getDashboardTitle(dashboard);
        ctrl.dashboard = dashboard;
        console.log('Dashboard', dashboard);
    }
})();
