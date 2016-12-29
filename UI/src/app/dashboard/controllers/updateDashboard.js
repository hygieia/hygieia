/**
 * Controller for the modal popup when updating
 * an existing dashboard on the dashboards main page
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('UpdateDashboardController', UpdateDashboardController);

    UpdateDashboardController.$inject = ['dashboardData', '$route', '$modalInstance', 'dashboard'];
    function UpdateDashboardController(dashboardData, $route, $modalInstance, dashboard) {
        var ctrl = this;

        // public variables
        ctrl.widgetsForm = dashboard.activeWidgetTypes;
        
        // public methods
        ctrl.submit = submit;
        
        // method implementations
        function submit(form) {
        	form.$setValidity('updateResponse', true);
        	
            // perform basic validation and send to the api
            if (form.$valid) {
            	var submitData = {
                        activeWidgetTypes: ctrl.widgetsForm,
                        type: dashboard.type,
                        title: dashboard.title,
                        owner: dashboard.owner,
                        applicationName: dashboard.application.name,
                        componentName: dashboard.application.name
                    };

            	dashboardData
                    .update(dashboard.id, submitData)
                    .success(function (data) {
                    	$route.reload();
                    	// close dialog
                        $modalInstance.dismiss();
                    })
                    .error(function (data) {
                        // display error message
                    	form.$setValidity('updateResponse', false);
                    });
            }
        }
    }
})();