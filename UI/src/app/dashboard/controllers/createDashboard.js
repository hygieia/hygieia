/**
 * Controller for the modal popup when creating
 * a new dashboard on the startup page
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('CreateDashboardController', CreateDashboardController);

    CreateDashboardController.$inject = ['$location', '$modalInstance', 'dashboardData', '$cookies', 'DashboardType'];
    function CreateDashboardController($location, $modalInstance, dashboardData, $cookies, DashboardType) {
        var ctrl = this;

        // public variables
        ctrl.templateName = '';
        ctrl.dashboardName = '';
        ctrl.applicationName = '';
        ctrl.submitted = false;


        // TODO: dynamically register templates with script
        ctrl.templates = [
            {value: 'capone', name: 'Cap One', type: DashboardType.TEAM},
            {value: 'caponechatops', name: 'Cap One ChatOps', type: DashboardType.TEAM},
            {value: 'splitview', name: 'Split View', type: DashboardType.TEAM},
            {value: 'product-dashboard', name: 'Product Dashboard', type: DashboardType.PRODUCT}
        ];

        // public methods
        ctrl.submit = submit;
        ctrl.isTeamDashboardSelected = isTeamDashboardSelected;
        ctrl.templateFilter = templateFilter;


        (function() {
            var types = dashboardData.types();
            ctrl.dashboardTypes = [];

            _(types).forEach(function(i) {
                ctrl.dashboardTypes.push({
                    id: i.id,
                    text: i.name + ' dashboard'
                })
            });

            if(ctrl.dashboardTypes.length) {
                ctrl.dashboardType = ctrl.dashboardTypes[0];
            }
        })();

        function templateFilter(item) {
            return !ctrl.dashboardType || item.type == ctrl.dashboardType.id;
        }

        // method implementations
        function submit(valid) {
            ctrl.submitted = true;


            // perform basic validation and send to the api
            if (valid) {
                var appName = document.cdf.applicationName ? document.cdf.applicationName.value : document.cdf.dashboardType.value,
                    submitData = {
                        template: document.cdf.templateName.value,
                        title: document.cdf.dashboardName.value,
                        type: document.cdf.dashboardType.value,
                        applicationName: appName,
                        componentName: appName,
                        owner: $cookies.username
                    };

                dashboardData
                    .create(submitData)
                    .then(function (data) {
                        // TODO: error handling
                        // redirect to the new dashboard
                        $location.path('/dashboard/' + data.id);
                    });

                // close dialog
                $modalInstance.dismiss();
            }
        }

        function isTeamDashboardSelected() {
            return ctrl.dashboardType && ctrl.dashboardType.id == DashboardType.TEAM;
        }
    }
})();