/**
 * Code Analysis widget configuration
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('CodeAnalysisConfigController', CodeAnalysisConfigController);

    CodeAnalysisConfigController.$inject = ['modalData', '$scope', 'collectorData', '$uibModalInstance'];
    function CodeAnalysisConfigController(modalData, $scope, collectorData, $uibModalInstance) {
        var ctrl = this,
        widgetConfig = modalData.widgetConfig,
        component = modalData.dashboard.application.components[0];

        ctrl.saToolsDropdownPlaceholder = 'Loading Security Analysis Jobs...';
        ctrl.ossToolsDropdownPlaceholder = 'Loading Open Source Scanning Jobs...';
        ctrl.testToolsDropdownPlaceholder = 'Loading Functional Test Jobs...';

        // public methods
        ctrl.caLoading = true;
        ctrl.submit = submitForm;
        ctrl.addTestConfig = addTestConfig;
        ctrl.deleteTestConfig = deleteTestConfig;

        $scope.getCodeQualityCollectors = function(filter){
        	return collectorData.itemsByType('codequality', {"search": filter, "size": 20}).then(function (response){
        		return response;
        	});
        };

        $scope.getSACollectors = function(filter){
            return collectorData.itemsByType('staticSecurityScan', {"search": filter, "size": 20}).then(function (response){
                return response;
            });
        };

        $scope.getOpenSourceCodeCollectors = function(filter){
            return collectorData.itemsByType('libraryPolicy', {"search": filter, "size": 20}).then(function (response){
                return response;
            });
        };

        loadSavedCodeQualityJob();
        loadSavedSAJob();
        loadSavedOpenSourceCodeJob();

        console.log(collectorData);
        // request all the codequality and test collector items
        collectorData.itemsByType('staticSecurityScan').then(processSaResponse);
        collectorData.itemsByType('test').then(processTestsResponse);
        collectorData.itemsByType('libraryPolicy').then(processOSSscanResponse);

        function loadSavedCodeQualityJob(){
        	var codeQualityCollectorItems = component.collectorItems.CodeQuality,
            savedCodeQualityJob = codeQualityCollectorItems ? codeQualityCollectorItems[0].description : null;

            if(savedCodeQualityJob){
            	$scope.getCodeQualityCollectors(savedCodeQualityJob).then(getCodeQualityCollectorsCallback) ;
            }
        }

        function loadSavedSAJob(){
            var saCollectorItems = component.collectorItems.StaticSecurityScan,
                savedSAJob = saCollectorItems ? saCollectorItems[0].description : null;

            if(savedSAJob){
                $scope.getSACollectors(savedSAJob).then(getSACollectorsCallback) ;
            }
        }

        function loadSavedOpenSourceCodeJob(){
            var ossCollectorItems = component.collectorItems.LibraryPolicy,
                savedOSSJob = ossCollectorItems ? ossCollectorItems[0].description : null;

            if(savedOSSJob){
                $scope.getOpenSourceCodeCollectors(savedOSSJob).then(getOpenSourceCodeCollectorsCallback) ;
            }
        }

        function getOpenSourceCodeCollectorsCallback(data) {
            ctrl.ossCollectorItem = data[0];
        }


        function getSACollectorsCallback(data) {
            ctrl.saCollectorItem = data[0];
        }

        function getCodeQualityCollectorsCallback(data) {
            ctrl.caCollectorItem = data[0];
        }

        function processSaResponse(data) {
            var saCollectorItems = component.collectorItems.StaticSecurityScan;
            var saCollectorItemId = _.isEmpty(saCollectorItems) ? null : saCollectorItems[0].id;

            ctrl.saJobs = data;
            ctrl.saCollectorItem = saCollectorItemId ? _.find(ctrl.saJobs, {id: saCollectorItemId}) : null;
            ctrl.saToolsDropdownPlaceholder = data.length ? 'Select a Security Analysis Job' : 'No Security Analysis Job Found';
        }

        function processOSSscanResponse(data) {
            var ossCollectorItems = component.collectorItems.LibraryPolicy;
            var ossCollectorItemId = _.isEmpty(ossCollectorItems) ? null : ossCollectorItems[0].id;

            ctrl.ossJobs = data;
            ctrl.ossCollectorItem = ossCollectorItemId ? _.find(ctrl.ossJobs, {id: ossCollectorItemId}) : null;
            ctrl.ossToolsDropdownPlaceholder = data.length ? 'Select a Open Source Scan Job' : 'No Open Source Scan Found';

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
                var testItem = testCollectorItemIds ? _.find(ctrl.testJobs, {id: testCollectorItemIds[index]}) : null;
                ctrl.testConfigs.push({
                    testJobName: testJobNamesFromWidget[index],
                    testJob: ctrl.testJobs,
                    testCollectorItem: testItem
                });
            }
            ctrl.testToolsDropdownPlaceholder = data.length ? 'Select a Functional Test Job' : 'No Functional Test Jobs Found';
        }

        function submitForm(caCollectorItem, saCollectorItem, ossCollectorItem, testConfigs) {
            var collectorItems = [];
            var testJobNames = [];
            if (caCollectorItem) collectorItems.push(caCollectorItem.id);
            if (saCollectorItem) collectorItems.push(saCollectorItem.id);
            if (ossCollectorItem) collectorItems.push(ossCollectorItem.id);
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
            $uibModalInstance.close(postObj);
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
