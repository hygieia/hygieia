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

        ctrl.widgetsForm = [];
        
        // public variables
        ctrl.dashboardTitle = '';
        ctrl.applicationName = '';
        ctrl.showIncludedWidgetsForm = true;
        
        // public methods
        ctrl.submit = submit;
        ctrl.isTeamDashboardSelected = isTeamDashboardSelected;
        ctrl.showDashboardTypeSpecificConfig = showDashboardTypeSpecificConfig;

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
        
        function showDashboardTypeSpecificConfig() {
        	ctrl.showIncludedWidgetsForm = isTeamDashboardSelected();
        }
		;

        // method implementations
        function submit(form) {
        	form.dashboardTitle.$setValidity('createError', true);
            // perform basic validation and send to the api
            if (form.$valid) {
                var appName = document.cdf.applicationName ? document.cdf.applicationName.value : document.cdf.dashboardType.value,
                    submitData = {
                        title: document.cdf.dashboardTitle.value,
                        type: document.cdf.dashboardType.value,
                        applicationName: appName,
                        componentName: appName,
                        owner: $cookies.username,
                        activeWidgetTypes: ctrl.widgetsForm
                    };

                dashboardData
                    .create(submitData)
                    .success(function (data) {
                        // redirect to the new dashboard
                        $location.path('/dashboard/' + data.id);
                        // close dialog
                        $modalInstance.dismiss();
                    })
                    .error(function (data) {
                        // display error message
                        form.dashboardTitle.$setValidity('createError', false);
                    });
            }
        }

        function isTeamDashboardSelected() {
            return ctrl.dashboardType && ctrl.dashboardType.id == DashboardType.TEAM;
        }
    }
})();