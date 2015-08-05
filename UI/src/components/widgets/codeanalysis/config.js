/**
 * Code Analysis widget configuration
 */
(function () {
    'use strict';

    angular
        .module('devops-dashboard')
        .controller('CodeAnalysisConfigController', CodeAnalysisConfigController);

    CodeAnalysisConfigController.$inject = ['modalData', 'collectorData', '$modalInstance'];
    function CodeAnalysisConfigController(modalData, collectorData, $modalInstance) {
        var ctrl = this;
        var widgetConfig = modalData.widgetConfig;
        var component = modalData.dashboard.application.components[0];

        ctrl.caToolsDropdownPlaceholder = 'Loading Code Analysis Jobs...';
        ctrl.testToolsDropdownPlaceholder = 'Loading Functional Test Jobs...';

        // public methods
        ctrl.submit = submitForm;

        // request all the codequality and test collector items
        collectorData.itemsByType('codequality').then(processCaResponse);
        collectorData.itemsByType('test').then(processTestsResponse);

        function processCaResponse(data) {
            var caCollectorItems = component.collectorItems.CodeQuality;
            var caCollectorItemId = _.isEmpty(caCollectorItems) ? null : caCollectorItems[0].id;

            ctrl.caJobs = data;
            ctrl.caCollectorItem = caCollectorItemId ? _.findWhere(ctrl.caJobs, { id: caCollectorItemId }) : null;
            ctrl.caToolsDropdownPlaceholder = 'Select a Code Analysis Job';
        }

        function processTestsResponse(data) {
            var testCollectorItems = component.collectorItems.Test;
            var testCollectorItemId = _.isEmpty(testCollectorItems) ? null : testCollectorItems[0].id;

            ctrl.testJobs = data;
            ctrl.testCollectorItem = testCollectorItemId ? _.findWhere(ctrl.testJobs, { id: testCollectorItemId }) : null;
            ctrl.testToolsDropdownPlaceholder = data.length ? 'Select a Functional Test Job' : 'No Functional Test Jobs found';
        }

        function submitForm(caCollectorItem, testCollectorItem) {
            var collectorItems = [caCollectorItem.id];
            if (testCollectorItem) collectorItems.push(testCollectorItem.id);

            var postObj = {
                name: 'codeanalysis',
                options: {
                    id: widgetConfig.options.id
                },
                componentId: component.id,
                collectorItemIds: collectorItems
            };

            // pass this new config to the modal closing so it's saved
            $modalInstance.close(postObj);
        }
    }
})();