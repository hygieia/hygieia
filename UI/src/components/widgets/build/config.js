/**
 * Build widget configuration
 */
(function () {
    'use strict';
    angular
        .module(HygieiaConfig.module)
        .controller('BuildWidgetConfigController', BuildWidgetConfigController);
    BuildWidgetConfigController.$inject = ['modalData', '$scope', 'collectorData', '$modalInstance', '$http'];
    function BuildWidgetConfigController(modalData, $scope, collectorData, $modalInstance, $http) {
      var ctrl = this;
      var widgetConfig = modalData.widgetConfig;

        // public variables
        ctrl.toolsDropdownPlaceholder = 'Loading Build Jobs...';
        ctrl.toolsDropdownDisabled = true;

        ctrl.buildDurationThreshold = 3;
        ctrl.buildConsecutiveFailureThreshold = 5;
        ctrl.formType = "list";
        $scope.jobs = ctrl.buildJobs;
        $scope.sortBy = "name";

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
        ctrl.submitUrl = submitJobUrl;

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
                    var url = obj.options.instanceUrl;
                    var index = url.search("job");

                    var item = {
                        value: obj.id,
                        name: ((obj.niceName != null) && (obj.niceName != "") ? obj.niceName + '-' + obj.description :  obj.description),
                        collector: obj.collector.name,
                        location: url.substring(index + 4, url.length)
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
                // console.log(form);
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

        function submitJobUrl(valid) {
         if (valid) {
            var form = document.buildConfigFormURL;
            var postObj = {
               collectorName: 'Hudson',
               buildServerUrl: form.buildServerUrl.value
            };
         // post object to server list
            console.log(postObj);
            $modalInstance.dismiss(
               $http({
                  method: 'POST',
                  url: '/api/build/server',
                  data: postObj
               }).then(alert("Your job folder was added to the collection list. It can take up to five minutes for the list to update. Please check the build list drop down in a few minutes, and then select the build you wish to monitor."))
            );
         }
        }

        $scope.setItem = function(item){
          ctrl.selectedItem = item;
          ctrl.collectorItemId = item.value;
          ctrl.itemName = item.name;
        }
        $scope.setSelectedItem = function(item){
          if(item == ctrl.selectedItem)
            return {"background-color": "rgba(51,185,28,.5)"};
        }
    }
})();
