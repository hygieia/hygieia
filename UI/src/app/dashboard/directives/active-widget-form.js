/**
 * Checkbox group for dashboard config regarding active widgets
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .controller('ActiveWidgetFormController', ActiveWidgetFormController)
        .directive('activeWidgetForm', function () {
            return {
            	controller: 'ActiveWidgetFormController',
            	controllerAs: 'ctrl',
            	templateUrl: 'app/dashboard/views/active-widget-form.html',
            	restrict: 'E',
            	scope: {
            		widgets: '='
            	},
            	link: function(scope, element, attrs, ctrl) {
            		if (!scope.widgets || scope.widgets.length == 0) {
            			// default behavior - set all widgets in config screen to active
        	        	ctrl.widgets.forEach(function(widget) {
        	        		ctrl.activeWidgetTypesSelection.push(widget.value);
        	        	});

        	        	ctrl.pipelineWidgets.forEach(function(widget) {
        	        		ctrl.activePipelineWidgetTypesSelection.push(widget.value);
        	        	});
        	        	
        	        	ctrl.cloudWidgets.forEach(function(widget) {
        	        		ctrl.activeCloudWidgetTypesSelection.push(widget.value);
        	        	});

        	        	scope.widgets = ctrl.getActiveWidgetMap();
            		} else {
            			ctrl.activeWidgetTypesSelection = scope.widgets.widget;
            			ctrl.activePipelineWidgetTypesSelection = scope.widgets.pipeline;
            			ctrl.activeCloudWidgetTypesSelection = scope.widgets.cloud;
            		}
            	}
            };
        });
    
    ActiveWidgetFormController.$inject = [];
    function ActiveWidgetFormController() {
    	var ctrl = this;
    	
    	// public vars
    	ctrl.activeWidgetTypesSelection = [];
        
        ctrl.widgets = [
            {value: 'feature', name: 'Feature'},
            {value: 'build', name: 'Build'},
            {value: 'monitor', name: 'Monitor'},
            {value: 'repo', name: 'Code Repo'},
            {value: 'codeanalysis', name: 'Quality'},
            {value: 'deploy', name: 'Deploy'},
            {value: 'chatops', name: 'Chat Ops'}
        ];
        
        ctrl.activePipelineWidgetTypesSelection = [];
        ctrl.pipelineWidgets = [
            {value: 'pipeline', name: 'Delivery Pipeline'}
        ];
        
        ctrl.activeCloudWidgetTypesSelection = [];
        ctrl.cloudWidgets = [
            {value: 'cloud', name: 'Cloud'}
        ];
        
        // public methods
        ctrl.toggleWidgetSelection = toggleWidgetSelection;
        ctrl.getActiveWidgetMap = getActiveWidgetMap;
        
        function getActiveWidgetMap() {
        	return {'widget': ctrl.activeWidgetTypesSelection, 'cloud': ctrl.activeCloudWidgetTypesSelection, 'pipeline': ctrl.activePipelineWidgetTypesSelection};
        }
        
        function toggleWidgetSelection(widgetName, selectionList) {
			var idx = selectionList.indexOf(widgetName);

			if (idx > -1) {
				selectionList.splice(idx, 1);
			} else {
				selectionList.push(widgetName);
			}
		}
    }
    	
})();