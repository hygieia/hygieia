/**
 * Build widget configuration
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('BuildWidgetConfigController', BuildWidgetConfigController);

    BuildWidgetConfigController.$inject = ['modalData', '$scope', 'collectorData', '$modalInstance'];
    function BuildWidgetConfigController(modalData, $scope, collectorData, $modalInstance) {
        var ctrl = this;
        var widgetConfig = modalData.widgetConfig;

        // public variables
        ctrl.toolsDropdownPlaceholder = 'Loading Build Jobs...';
        ctrl.toolsDropdownDisabled = true;

        ctrl.buildDurationThreshold = 3;
        ctrl.buildConsecutiveFailureThreshold = 5;

        ctrl.oridata = null;
        ctrl.loading = true;
        ctrl.paginationRange = 100;




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
        ctrl.fetch = paginationFetch;

        // request all the build collector items
        collectorData.itemsByType('build').then(processResponse);

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
                        name: ((obj.niceName != null) && (obj.niceName != "") ? obj.niceName + '-' + obj.description : obj.collector.name + '-' + obj.description),
                        group: ((obj.niceName != null) && (obj.niceName != "") ? obj.niceName : obj.collector.name)
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
            ctrl.oridata = data.builds;
                ctrl.toolsDropdownPlaceholder = 'Select a Build Job';
                ctrl.toolsDropdownDisabled = false;

                if (data.selectedIndex !== null) {
                    ctrl.collectorItemId = ctrl.buildJobs[data.selectedIndex];
                }
            //});
        }


        function paginationFetch($select, $event, x) {
            if (!$event) {
                console.log("called first time")
            } else {
                $event.stopPropagation();
                $event.preventDefault();
                console.log("called subsequent time");
                updatePaginationVariables(ctrl.oridata, x);
            }

        }


        function updatePaginationVariables(m, startIndex) {

            console.log("Before:" + m.length);
            var y = [];

            if (m.length <= 200) {
                y = m;
                ctrl.loading = false;
            }
            else {
                for (var p = 0; p < ctrl.paginationRange; p++) {
                    var value = {
                        value: m[p].value,
                        name: m[p].name
                    }

                    y.push(value);
                    m.shift();
                }
            }

            //console.log("Y is :" + JSON.stringify(y));

            $scope.$applyAsync(function () {
                ctrl.buildJobs = y;
            });

            console.log("After:" + m.length);

        }

        function submitForm(valid, collector) {
            console.log("Collector" + JSON.stringify(collector));
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
                    collectorItemId: collector.value
                };


                console.log(modalData.dashboard.application.components[0].id);

                // pass this new config to the modal closing so it's saved
                $modalInstance.close(postObj);
            }
        }
    }
})();