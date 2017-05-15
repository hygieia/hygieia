/**
 * Build widget configuration
 */
(function () {
    'use strict';
    angular
        .module(HygieiaConfig.module)
        .controller('BuildWidgetConfigController', BuildWidgetConfigController);
    BuildWidgetConfigController.$inject = ['modalData', '$scope', 'collectorData', '$uibModalInstance'];
    function BuildWidgetConfigController(modalData, $scope, collectorData, $uibModalInstance) {
        var ctrl = this,
        widgetConfig = modalData.widgetConfig;
        
        // public variables
        ctrl.buildDurationThreshold = 3;
        ctrl.buildConsecutiveFailureThreshold = 5;
        
        $scope.getJobs = function (filter) {
        	return collectorData.itemsByType('build', {"search": filter, "size": 20}).then(function (response){
        		return response;
        	});
        }
        
        loadSavedBuildJob();
        // set values from config
        if (widgetConfig) {
            if (widgetConfig.options.buildDurationThreshold) {
                ctrl.buildDurationThreshold = widgetConfig.options.buildDurationThreshold;
            }
            if (widgetConfig.options.consecutiveFailureThreshold) {
                ctrl.buildConsecutiveFailureThreshold = widgetConfig.options.consecutiveFailureThreshold;
            }
        }
        // public methods
        ctrl.submit = submitForm;

        // method implementations
        function loadSavedBuildJob(){
        	var buildCollector = modalData.dashboard.application.components[0].collectorItems.Build,
            savedCollectorBuildJob = buildCollector ? buildCollector[0].description : null;
            if(savedCollectorBuildJob) { 
            	$scope.getJobs(savedCollectorBuildJob).then(getBuildsCallback) 
            }
        }
        
        function getBuildsCallback(data) {
            ctrl.collectorItemId = data[0];
        }

        function submitForm(valid, collector) {
            if (valid) {
                var form = document.buildConfigForm;
                var postObj = {
                    name: 'build',
                    options: {
                    	id: widgetConfig.options.id,
                        buildDurationThreshold: parseFloat(form.buildDurationThreshold.value),
                        consecutiveFailureThreshold: parseFloat(form.buildConsecutiveFailureThreshold.value)
                    },
                    componentId: modalData.dashboard.application.components[0].id,
                    collectorItemId: collector.id,
                };
                // pass this new config to the modal closing so it's saved
                $uibModalInstance.close(postObj);
            }
        }
    }
})();
