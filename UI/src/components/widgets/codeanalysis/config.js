/**
 * Code Analysis widget configuration
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('CodeAnalysisConfigController', CodeAnalysisConfigController);

    CodeAnalysisConfigController.$inject = ['modalData', 'collectorData', '$modalInstance'];
    function CodeAnalysisConfigController(modalData, collectorData, $modalInstance) {
        var ctrl = this;
        var widgetConfig = modalData.widgetConfig;
        var component = modalData.dashboard.application.components[0];

        ctrl.caToolsDropdownPlaceholder = 'Loading Code Analysis Jobs...';
        ctrl.saToolsDropdownPlaceholder = 'Loading Security Analysis Jobs...';
        ctrl.testToolsDropdownPlaceholder = 'Loading Functional Test Jobs...';

        // public methods
        ctrl.submit = submitForm;
        ctrl.addTestConfig = addTestConfig;
        ctrl.deleteTestConfig = deleteTestConfig;

        // request all the codequality and test collector items
        collectorData.itemsByType('codequality').then(processCaResponse);
        collectorData.itemsByType('staticSecurityScan').then(processSaResponse);
        collectorData.itemsByType('test').then(processTestsResponse);

        function processCaResponse(data) {

            var caCollectorItems = component.collectorItems.CodeQuality;
            var caCollectorItemId = _.isEmpty(caCollectorItems) ? null : caCollectorItems[0].id;
            if (data != null) {
                var j;
                for (j = 0; j < data.length; ++j) {
                    data[j].displayName = ((data[j].niceName != null) && (data[j].niceName != ""))? data[j].niceName : data[j].collector.name;
                }
            }
            ctrl.caJobs = data;

            ctrl.caCollectorItem = caCollectorItemId ? _.findWhere(ctrl.caJobs, {id: caCollectorItemId}) : null;
            ctrl.caToolsDropdownPlaceholder = data.length ? 'Select a Code Analysis Job' : 'No Code Analysis Job Found';
        }

        function processSaResponse(data) {
            var saCollectorItems = component.collectorItems.StaticSecurityScan;
            var saCollectorItemId = _.isEmpty(saCollectorItems) ? null : saCollectorItems[0].id;

            ctrl.saJobs = data;
            ctrl.saCollectorItem = saCollectorItemId ? _.findWhere(ctrl.saJobs, {id: saCollectorItemId}) : null;
            ctrl.saToolsDropdownPlaceholder = data.length ? 'Select a Security Analysis Job' : 'No Security Analysis Job Found';
        }

        function processTestsResponse(data) {
            ctrl.testJobs = data;
            ctrl.testConfigs = [];
            var testCollectorItems = component.collectorItems.Test;
            var testCollectorItemIds = [];
            var testJobNamesFromWidget = [];
            // set values from config
            if (widgetConfig) {
                if (widgetConfig.options.testJobNames) {
                    var j;
                    for (j = 0; j < widgetConfig.options.testJobNames.length; ++j) {
                        testJobNamesFromWidget.push(widgetConfig.options.testJobNames[j]);
                    }
                }
            }
            var index;
            if (testCollectorItems != null) {
                for (index = 0; index < testCollectorItems.length; ++index) {
                    testCollectorItemIds.push(testCollectorItems[index].id);
                }
            }
            for (index = 0; index < testCollectorItemIds.length; ++index) {
                var testItem = testCollectorItemIds ? _.findWhere(ctrl.testJobs, {id: testCollectorItemIds[index]}) : null;
                ctrl.testConfigs.push({
                    testJobName: testJobNamesFromWidget[index],
                    testJob: ctrl.testJobs,
                    testCollectorItem: testItem
                });
            }
            ctrl.testToolsDropdownPlaceholder = data.length ? 'Select a Functional Test Job' : 'No Functional Test Jobs Found';
        }

        function submitForm(caCollectorItem, saCollectorItem, testConfigs) {
            var collectorItems = [];
            var testJobNames = [];
            if (caCollectorItem) collectorItems.push(caCollectorItem.id);
            if (saCollectorItem) collectorItems.push(saCollectorItem.id);
            if (testConfigs) {
                var index;
                for (index = 0; index < testConfigs.length; ++index) {
                    collectorItems.push(testConfigs[index].testCollectorItem.id);
                    testJobNames.push(testConfigs[index].testJobName);
                }
            }
            var form = document.configForm;
            var postObj = {
                name: 'codeanalysis',
                options: {
                    id: widgetConfig.options.id,
                    testJobNames: testJobNames
                },
                componentId: component.id,
                collectorItemIds: collectorItems
            };
            // pass this new config to the modal closing so it's saved
            $modalInstance.close(postObj);
        }


        function addTestConfig() {
            var newItemNo = ctrl.testConfigs.length + 1;
            ctrl.testConfigs.push({testJobName: 'Name' + newItemNo, testJob: ctrl.testJobs, testCollectorItem: null});
        }

        function deleteTestConfig(item) {
            ctrl.testConfigs.pop(item);
        }
    }
})();