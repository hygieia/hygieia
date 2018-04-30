/**
 * Rally widget configuration
 */
(function() {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('RallyWidgetConfigController', RallyWidgetConfigController);
    RallyWidgetConfigController.$inject = ['modalData', '$scope', 'rallyData', 'collectorData', '$uibModalInstance'];
    function RallyWidgetConfigController(modalData, $scope, rallyData, collectorData, $uibModalInstance) {
        var ctrl = this;
        var widgetConfig = modalData.widgetConfig;
        // public variables
        ctrl.toolsDropdownPlaceholder = 'Loading Projects...';
        ctrl.toolsDropdownDisabled = true;
        ctrl.oridata = null;
        ctrl.loading = true;
        // public methods
        ctrl.submit = submitForm;
        // request all the rally collector items
        collectorData.itemsByType('AgileTool').then(processResponse);
        // method implementations
        function processResponse(data) {
            ctrl.toolsDropdownPlaceholder = 'Select the project';
            ctrl.toolsDropdownDisabled = false;
            var filteredData = _.uniq(data, 'description');
            ctrl.rallyProjects = filteredData;
            var selectedIndex = _.findIndex(filteredData, function(selectedItem) { return selectedItem.options.projectId == widgetConfig.options.projectId});
            ctrl.rallyProject = filteredData[selectedIndex];
            ctrl.rallySelectedIteration = true;

        }
        function submitForm(valid, collector) {
            if (valid) {
            var rallyCollector = modalData.dashboard.application.components[0].collectorItems.AgileTool;
            var rallyCollectorId = rallyCollector ? rallyCollector[0].id : null;
            var projectId= ctrl.rallyProject.options.projectId;
            var projectName= ctrl.rallyProject.options.projectName;
                var postObj = {
                    name: 'AgileTool',
                    options: {
                        id: widgetConfig.options.id,
                        projectId:projectId,
                        projectName:projectName
                    },
                    componentId: modalData.dashboard.application.components[0].id,
                    collectorItemId: collector
                };
                // pass this new config to the modal closing so it's saved
                $uibModalInstance.close(postObj);
            }
        }
    }
})();
