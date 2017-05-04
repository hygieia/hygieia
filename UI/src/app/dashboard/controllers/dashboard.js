/**
 * Controller for the dashboard route.
 * Render proper template.
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('DashboardController', DashboardController);

    DashboardController.$inject = ['dashboard', '$location'];
    function DashboardController(dashboard, $location) {
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
        ctrl.dashboard = dashboard;

        console.log('Dashboard', dashboard);
    }
})();
