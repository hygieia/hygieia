/**
 * Controller for the modal popup when creating
 * a new dashboard on the startup page
 */
(function () {
    'use strict';

    angular
        .module('devops-dashboard')
        .controller('CreateDashboardController', CreateDashboardController);

    CreateDashboardController.$inject = ['$location', '$modalInstance', 'dashboardData', '$cookies'];
    function CreateDashboardController($location, $modalInstance, dashboardData, $cookies) {
        var ctrl = this;

        // public variables
        ctrl.templateName = '';
        ctrl.dashboardName = '';
        ctrl.applicationName = '';
        ctrl.submitted = false;


        // TODO: dynamically register templates with script
        ctrl.templates = [
            {value: 'capone', name: 'Cap One'},
            {value: 'caponechatops', name: 'Cap One ChatOps'},
            {value: 'splitview', name: 'Split View'}
        ];

        // public methods
        ctrl.submit = submit;
        ctrl.isTeamDashboardSelected = isTeamDashboardSelected;


        dashboardData.types().then(function(response) {
            ctrl.dashboardTypes = [];

            _(response).forEach(function(i) {
                ctrl.dashboardTypes.push({
                    id: i.id,
                    text: i.name + ' dashboard'
                })
            });

            if(ctrl.dashboardTypes.length) {
                ctrl.dashboardType = ctrl.dashboardTypes[0];
            }
        });

        // method implementations
        function submit(valid) {
            ctrl.submitted = true;

            // perform basic validation and send to the api
            if (valid) {
                var submitData = {
                    template: document.cdf.templateName.value,
                    title: document.cdf.dashboardName.value,
                    applicationName: document.cdf.applicationName ? document.cdf.applicationName.value : null,
                    componentName: document.cdf.applicationName.value,
                    type: document.cdf.dashboardType.value,
                    owner: $cookies.username
                };

                console.log(submitData);
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
            return ctrl.dashboardType && ctrl.dashboardType.id == 1;
        }
    }
})();