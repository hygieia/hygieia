/**
 * Code Analysis widget configuration
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('LogConfigController', LogAnalysisConfigController);

    LogAnalysisConfigController.$inject = ['modalData', '$scope', 'collectorData', '$uibModalInstance'];
    function LogAnalysisConfigController(modalData, $scope, collectorData, $uibModalInstance) {
        var ctrl = this;
        var widgetConfig = modalData.widgetConfig;
        var component = modalData.dashboard.application.components[0];

        $scope.getLogCollectors = function(filter){
            return collectorData.itemsByType('log', {"search": filter, "size": 20}).then(function (response){
                return response;
            });
        };

        loadSavedLogCollectorItemsJob();

        function loadSavedLogCollectorItemsJob(){
            var logCollectorItems = component.collectorItems.Log,
                logCollectorItems = logCollectorItems ? logCollectorItems[0].name : null;

            if(logCollectorItems){
                $scope.getLogCollectors(logCollectorItems).then(getLogCollectorItemsCallback) ;
            }
        }

        function getLogCollectorItemsCallback(data) {
            ctrl.logCollectorItem = data[0];
        }
    }
})();