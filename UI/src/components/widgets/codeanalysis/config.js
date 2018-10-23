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

        $scope.getJobsById = function (id) {
            return collectorData.getCollectorItemById(id).then(function (response){
                return response;
            });
        }

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
        $scope.getTestCollectors = function(filter){
            return collectorData.itemsByType('test', {"search": filter, "size": 20}).then(function (response){
                return response;
            });
        };

        loadSavedCodeQualityJob();
        loadSavedSAJob();
        loadSavedOpenSourceCodeJob();
        loadSavedTestJobs();

        function loadSavedCodeQualityJob(){
        	var codeQualityCollectorItems = component.collectorItems.CodeQuality,
            savedCodeQualityJob = codeQualityCollectorItems ? codeQualityCollectorItems[0] : null;

            if(savedCodeQualityJob){
                $scope.getJobsById(savedCodeQualityJob.id).then(getCodeQualityCollectorsCallback)
            }
        }

        function loadSavedSAJob(){
            var saCollectorItems = component.collectorItems.StaticSecurityScan,
                savedSAJob = saCollectorItems ? saCollectorItems[0] : null;

            if(savedSAJob){

                $scope.getJobsById(savedSAJob.id).then(getSACollectorsCallback) ;
            }
        }

        function loadSavedOpenSourceCodeJob(){
            var ossCollectorItems = component.collectorItems.LibraryPolicy,
                savedOSSJob = ossCollectorItems ? ossCollectorItems[0] : null;

            if(savedOSSJob){
                $scope.getJobsById(savedOSSJob.id).then(getOpenSourceCodeCollectorsCallback) ;
            }
        }
        function loadSavedTestJobs() {
            ctrl.testConfigs = []
            var testCollectorItems = component.collectorItems.Test;

            if (testCollectorItems != null) {

                testCollectorItems.forEach(function(obj) {
                   
                    ctrl.testConfigs.push({
                        testCollectorItem: obj
                    })});
            }
        }
        function getOpenSourceCodeCollectorsCallback(data) {
            ctrl.ossCollectorItem = data;
        }


        function getSACollectorsCallback(data) {
            ctrl.saCollectorItem = data;
        }

        function getCodeQualityCollectorsCallback(data) {
            ctrl.caCollectorItem = data;
        }

        function submitForm(caCollectorItem, saCollectorItem, ossCollectorItem, testConfigs) {

            var collectorItems = [];
            if (caCollectorItem) collectorItems.push(caCollectorItem.id);
            if (saCollectorItem) collectorItems.push(saCollectorItem.id);
            if (ossCollectorItem) collectorItems.push(ossCollectorItem.id);
            if (testConfigs) {
                var index;
                for (index = 0; index < testConfigs.length; ++index) {
                    collectorItems.push(testConfigs[index].testCollectorItem.id);
                }
            }
            var form = document.configForm;
            var postObj = {
                name: 'codeanalysis',
                options: {
                    id: widgetConfig.options.id
                  },
                componentId: component.id,
                collectorItemIds: collectorItems
            };
            // pass this new config to the modal closing so it's saved
            $uibModalInstance.close(postObj);
        }


        function addTestConfig() {
            var newItemNo = ctrl.testConfigs.length + 1;
            ctrl.testConfigs.push({testJob: ctrl.testJobs, testCollectorItem: null});
        }

        function deleteTestConfig(item) {
            ctrl.testConfigs.splice(ctrl.testConfigs.indexOf(item), 1);
        }
    }
})();
