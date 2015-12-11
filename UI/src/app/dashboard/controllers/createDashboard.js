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

        // To tie dashboard to owner
        ctrl.owner='';


        // TODO: dynamically register templates with script
        ctrl.templates = [
            {value: 'capone', name: 'Cap One'},
            {value: 'caponechatops', name: 'Cap One ChatOps'},
            {value: 'splitview', name: 'Split View'},

        ];

        // public methods
        ctrl.submit = submit;


        // method implementations
        function submit(valid) {
            ctrl.submitted = true;

            //Get the signed in user from the cookie store
           ctrl.owner=$cookies.username;
            console.log("Owner in dashboard is"+ ctrl.owner);

            // perform basic validation and send to the api
            if (valid) {
                dashboardData
                    .create({
                        template: document.cdf.templateName.value,
                        title: document.cdf.dashboardName.value,
                        applicationName: document.cdf.applicationName.value,
                        componentName: document.cdf.applicationName.value,
                        owner: ctrl.owner
                    })
                    .then(function (data) {
                        // TODO: error handling
                        // redirect to the new dashboard
                        $location.path('/dashboard/' + data.id);
                    });

                // close dialog
                $modalInstance.dismiss();
            }
        }
    }
})();