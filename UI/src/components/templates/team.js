/**
 * Controller for the dashboard route.
 * Render proper template.
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('TemplateController', TemplateController);

    TemplateController.$inject = ['$scope', '$uibModal', '$cookies', 'dashboardData'];
    function TemplateController($scope, $uibModal, $cookies, dashboardData) {
        var ctrl = this;

        ctrl.tabs = [
            { name: "Widget"},
            { name: "Pipeline"},
            { name: "Cloud"}
           ];

        ctrl.widgetView = ctrl.tabs[0].name;
        ctrl.toggleView = function (index) {
            ctrl.widgetView = typeof ctrl.tabs[index] === 'undefined' ? ctrl.tabs[0].name : ctrl.tabs[index].name;
        };
        
        ctrl.showSettings = function(dashboard) {
            configModal(dashboard);
		}
        
        // TODO: Re-do this function once https://github.com/capitalone/Hygieia/pull/1129 is accepted.
        ctrl.isOwner = function(owner) {
        	if (owner == $cookies.get('username') || $cookies.get('username') == 'admin') {
                return true;
            }
	        
            return false;
        }

        // If activeWidgetTypes does not exist, show all widgets due to being a legacy dashboard
        // Otherwise, if it does exist and is empty, show help text
        ctrl.showEmptyContainerText = function(dashboard) {
        	var isNotLegacy = dashboard.activeWidgetTypes;
        	if (isNotLegacy) {
        		var existingTypes = dashboard.activeWidgetTypes[ctrl.widgetView.toLowerCase()];
        		return existingTypes.length === 0;
        	}
        	
        	return isNotLegacy;
        }
        
        function configModal(dashboard) {
        	$uibModal.open({
                templateUrl: 'app/dashboard/views/updateDashboard.html',
                controller: 'UpdateDashboardController',
                controllerAs: 'ctrl',
                resolve: {
                	dashboard: function() {
                		return dashboard;
                	}
                }
            });
        }
    }
})();
