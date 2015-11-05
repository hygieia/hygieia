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
        ctrl.saToolsDropdownPlaceholder = 'Loading Security Analysis Jobs...';
        ctrl.testToolsDropdownPlaceholder = 'Loading Functional Test Jobs...';

        // public methods
        ctrl.submit = submitForm;

        // request all the codequality and test collector items
        collectorData.itemsByType('codequality').then(processCaResponse);
        collectorData.itemsByType('staticSecurityScan').then(processSaResponse);
        collectorData.itemsByType('test').then(processTestsResponse);

        function processCaResponse(data) {
            var caCollectorItems = component.collectorItems.CodeQuality;
            var caCollectorItemId = _.isEmpty(caCollectorItems) ? null : caCollectorItems[0].id;

            ctrl.caJobs = data;
            ctrl.caCollectorItem = caCollectorItemId ? _.findWhere(ctrl.caJobs, { id: caCollectorItemId }) : null;
            ctrl.caToolsDropdownPlaceholder = data.length ? 'Select a Code Analysis Job' : 'No Code Analysis Job Found';
        }

        function processSaResponse(data) {
            var saCollectorItems = component.collectorItems.StaticSecurityScan;
            var saCollectorItemId = _.isEmpty(saCollectorItems) ? null : saCollectorItems[0].id;

            ctrl.saJobs = data;
            ctrl.saCollectorItem = saCollectorItemId ? _.findWhere(ctrl.saJobs, { id: saCollectorItemId }): null;
            ctrl.saToolsDropdownPlaceholder = data.length ? 'Select a Security Analysis Job' : 'No Security Analysis Job Found';
        }
        function processTestsResponse(data) {
            var testCollectorItems = component.collectorItems.Test;
            var testCollectorItemId = _.isEmpty(testCollectorItems) ? null : testCollectorItems[0].id;

            ctrl.testJobs = data;
            ctrl.testCollectorItem = testCollectorItemId ? _.findWhere(ctrl.testJobs, { id: testCollectorItemId }) : null;
            ctrl.testToolsDropdownPlaceholder = data.length ? 'Select a Functional Test Job' : 'No Functional Test Jobs Found';
        }

        function submitForm(caCollectorItem, saCollectorItem, testCollectorItem) {
            var collectorItems = [];
            if (caCollectorItem) collectorItems.push(caCollectorItem.id);
            if (saCollectorItem) collectorItems.push(saCollectorItem.id);
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