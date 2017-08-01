/**
 * Controller for the modal popup when creating
 * a new dashboard on the startup page
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('CreateDashboardController', CreateDashboardController);

    CreateDashboardController.$inject = ['$location', '$uibModalInstance', 'dashboardData', 'userService', 'DashboardType','cmdbData', 'dashboardService'];
    function CreateDashboardController($location, $uibModalInstance, dashboardData, userService, DashboardType, cmdbData, dashboardService) {
        var ctrl = this;

        // public variables
        ctrl.dashboardTitle = '';
        ctrl.applicationName = '';
        ctrl.availableTemplates = [];
        ctrl.configurationItemBusServ = '';
        ctrl.configurationItemBusApp = '';
        ctrl.configurationItemBusServId = "";
        ctrl.configurationItemBusAppId = "";

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
        ctrl.resetFormValidation = resetFormValidation;
        ctrl.setConfigItemAppId = setConfigItemAppId;
        ctrl.setConfigItemComponentId = setConfigItemComponentId;
        ctrl.getBusAppToolText =  getBusAppToolText;
        ctrl.getBusSerToolText = getBusSerToolText;
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
            ctrl.configurationItemBusApp = dashboardService.getBusServValueBasedOnType(ctrl.dashboardType.id, ctrl.configurationItemBusApp);
            ctrl.availableTemplates = templates;
        }

        // method implementations
        function submit(form) {

            resetFormValidation(form);
            // perform basic validation and send to the api
            if (form.$valid) {
                var appName = document.cdf.applicationName ? document.cdf.applicationName.value : document.cdf.dashboardType.value,
                    submitData = {
                        template: document.cdf.selectedTemplate.value,
                        title: document.cdf.dashboardTitle.value,
                        type: document.cdf.dashboardType.value,
                        applicationName: appName,
                        componentName: appName,
                        configurationItemBusServObjectId: dashboardService.getBusinessServiceId(ctrl.configurationItemBusServ),
                        configurationItemBusAppObjectId:  dashboardService.getBusinessApplicationId(ctrl.configurationItemBusApp)
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
                        if(data.errorCode === 401) {
                          $modalInstance.close();
                        }else if(data.errorCode === -13){

                            if(data.errorMessage){
                                ctrl.dupErroMessage = data.errorMessage;
                            }

                            form.configurationItemBusServ.$setValidity('dupBusServError', false);
                            form.configurationItemBusApp.$setValidity('dupBusAppError', false);

                        }else{
                            form.dashboardTitle.$setValidity('createError', false);
                        }

                    });
            }
        }

        function isTeamDashboardSelected() {
            return ctrl.dashboardType && ctrl.dashboardType.id == DashboardType.TEAM;
        }

        function resetFormValidation(form){
            ctrl.dupErroMessage = "";
            form.configurationItemBusServ.$setValidity('dupBusServError', true);
            form.configurationItemBusApp.$setValidity('dupBusAppError', true);
            form.dashboardTitle.$setValidity('createError', true);
        }

        function setConfigItemAppId(id){
            dashboardService.setBusinessServiceId(id);
        }

        function setConfigItemComponentId(id){
            dashboardService.setBusinessApplicationId(id);
        }

        function getBusAppToolText(){
            return dashboardService.getBusAppToolTipText();
        }

        function getBusSerToolText(){
            return dashboardService.getBusSerToolTipText();
        }
    }
})();
