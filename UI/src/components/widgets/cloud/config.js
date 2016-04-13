/**
 * Created by hyw912 on 4/13/16.
 */

/**
 * Build widget configuration
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('CloudWidgetConfigController', CloudWidgetConfigController);

    CloudWidgetConfigController.$inject = ['modalData', '$scope', 'cloudData', '$modalInstance'];
    function CloudWidgetConfigController(modalData, $scope, cloudData, $modalInstance) {

        var ctrl = this;
        var widgetConfig = modalData.widgetConfig;

        // public variables
        ctrl.asvDropdownPlaceholder = 'Loading ASV List...';
        ctrl.asvDropdownDisabled = true;

        // public methods
        ctrl.submit = function (valid) {
            if (valid) {

                var postObj = {};

                /*var form = document.buildConfigForm;
                var postObj = {
                    name: 'build',
                    options: {
                        id: widgetConfig.options.id,
                        buildDurationThreshold: parseFloat(form.buildDurationThreshold.value),
                        consecutiveFailureThreshold: parseFloat(form.buildConsecutiveFailureThreshold.value)
                    },
                    componentId: modalData.dashboard.application.components[0].id,
                    collectorItemId: form.collectorItemId.value
                };
                */
                // pass this new config to the modal closing so it's saved
                $modalInstance.close(postObj);
            }
        }

        // public variables
      /*  ctrl.toolsDropdownPlaceholder = 'Loading Build Jobs...';
        ctrl.toolsDropdownDisabled = true;

        ctrl.buildDurationThreshold = 3;
        ctrl.buildConsecutiveFailureThreshold = 5;

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

        // request all the build collector items
        cloudData.itemsByType('build').then(processResponse);

        // method implementations
        function processResponse(data) {
            var worker = {
                getBuildJobs: getBuildJobs
            };

            function getBuildJobs(data, currentCollectorItemId, cb) {
                var builds = [],
                    selectedIndex = null;

                for (var x = 0; x < data.length; x++) {
                    var obj = data[x];
                    var item = {
                        value: obj.id,
                        name: ((obj.niceName != null) && (obj.niceName != "") ? obj.niceName + '-' + obj.description : obj.collector.name + '-' + obj.description)
                    };
                    builds.push(item);

                    if (currentCollectorItemId !== null && item.value == currentCollectorItemId) {
                        selectedIndex = x;
                    }
                }

                cb({
                    builds: builds,
                    selectedIndex: selectedIndex
                });
            }

            var buildCollector = modalData.dashboard.application.components[0].collectorItems.Build;
            var buildCollectorId = buildCollector ? buildCollector[0].id : null;
            worker.getBuildJobs(data, buildCollectorId, getBuildsCallback);
        }

        function getBuildsCallback(data) {
            //$scope.$apply(function () {
            ctrl.buildJobs = data.builds;
            ctrl.toolsDropdownPlaceholder = 'Select a Build Job';
            ctrl.toolsDropdownDisabled = false;

            if (data.selectedIndex !== null) {
                ctrl.collectorItemId = ctrl.buildJobs[data.selectedIndex];
            }
            //});
        }

        function submitForm(valid) {
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
                    collectorItemId: form.collectorItemId.value
                };

                // pass this new config to the modal closing so it's saved
                $modalInstance.close(postObj);
            }
        } */
    }
})();
