/**
 * Controller for the modal popup when creating
 * a new dashboard on the startup page
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('CreateDashboardController', CreateDashboardController);

    CreateDashboardController.$inject = ['$location', '$uibModalInstance', 'dashboardData', 'userService', 'DashboardType','cmdbData'];
    function CreateDashboardController($location, $uibModalInstance, dashboardData, userService, DashboardType, cmdbData) {
        var ctrl = this;

            // public variables
        ctrl.dashboardTitle = '';
        ctrl.applicationName = '';
        ctrl.availableTemplates = [];
        ctrl.configurationItemApp = '';
        ctrl.configurationItemComponent = '';
        ctrl.configurationItemAppId = "";
        ctrl.configurationItemComponentId = "";
        // TODO: dynamically register templates with script
        ctrl.templates = [
            {value: 'capone', name: 'Cap One', type: DashboardType.TEAM},
            {value: 'caponechatops', name: 'Cap One ChatOps', type: DashboardType.TEAM},
            {value: 'cloud', name: 'Cloud Dashboard', type: DashboardType.TEAM},
            {value: 'splitview', name: 'Split View', type: DashboardType.TEAM},
            {value: 'product-dashboard', name: 'Product Dashboard', type: DashboardType.PRODUCT}
        ];

        // public methods
        ctrl.submit = submit;
        ctrl.isTeamDashboardSelected = isTeamDashboardSelected;
        ctrl.templateFilter = templateFilter;
        ctrl.setAvailableTemplates = setAvailableTemplates;
        ctrl.getConfigItem = getConfigItem;
        ctrl.setConfigItemAppId = setConfigItemAppId;
        ctrl.setConfigItemComponentId = setConfigItemComponentId;
        ctrl.getConfigItemAppId = getConfigItemAppId;
        ctrl.getConfigItemComponentId = getConfigItemComponentId;

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
                ctrl.setAvailableTemplates();
            }
        })();

        function getConfigItem(type ,filter) {
            return cmdbData.getConfigItemList(type, {"search": filter, "size": 20}).then(function (response){
                return response;
            });
        }
        function templateFilter(item) {
            return !ctrl.dashboardType || item.type == ctrl.dashboardType.id;
        }

        function setAvailableTemplates()
        {
            var templates = [];
            ctrl.selectedTemplate = null;

            if(!!ctrl.dashboardType) {
                _(ctrl.templates).forEach(function(tmpl) {
                    if(tmpl.type === ctrl.dashboardType.id) {
                        templates.push(tmpl);
                    }
                });
            }

            if(templates.length == 1) {
                ctrl.selectedTemplate = templates[0];
            }

            ctrl.availableTemplates = templates;
        }

        function setConfigItemAppId(id){
            ctrl.configurationItemAppId = id;
        }

        function getConfigItemAppId(){
            var value = null;
            if(ctrl.configurationItemApp){
                value = ctrl.configurationItemAppId;
            }
            return value;
        }

        function setConfigItemComponentId(id){
            ctrl.configurationItemComponentId = id;
        }

        function getConfigItemComponentId(){
            var value = null;
            if(ctrl.configurationItemComponent){
                value = ctrl.configurationItemComponentId;
            }
            return value;
        }
        // method implementations
        function submit(form) {

            form.dashboardTitle.$setValidity('createError', true);
            // perform basic validation and send to the api
            if (form.$valid) {
                var appName = document.cdf.applicationName ? document.cdf.applicationName.value : document.cdf.dashboardType.value,
                    submitData = {
                        template: document.cdf.selectedTemplate.value,
                        title:  document.cdf.dashboardTitle.value,
                        type: document.cdf.dashboardType.value,
                        applicationName: appName,
                        componentName: appName,
                        configurationItemAppObjectId:  ctrl.getConfigItemAppId(),
                        configurationItemComponentObjectId:  ctrl.getConfigItemComponentId()
                    };

                dashboardData
                    .create(submitData)
                    .success(function (data) {
                        // redirect to the new dashboard
                        $location.path('/dashboard/' + data.id);
                        // close dialog
                        $uibModalInstance.dismiss();
                    })
                    .error(function (data) {
                        // display error message
                        form.dashboardTitle.$setValidity('createError', false);
                        if(data.status === 401) {
                          $modalInstance.close();
                        }
                    });
            }
        }

        function isTeamDashboardSelected() {
            return ctrl.dashboardType && ctrl.dashboardType.id == DashboardType.TEAM;
        }
    }
})();
