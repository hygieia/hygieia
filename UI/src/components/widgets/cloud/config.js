/**
 * Created by nmande on 4/13/16.
 */

/**
 * Build widget configuration
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('CloudWidgetConfigController', CloudWidgetConfigController);

    CloudWidgetConfigController.$inject = ['modalData', 'collectorData', '$modalInstance'];
    function CloudWidgetConfigController(modalData, collectorData, $modalInstance) {


        //private properties/methods
        var ctrl = this;
        var widgetConfig = modalData.widgetConfig;


        function createCloudConfigItem(accountNumber,tagName,tagValue) {
            var item = {
                collectorId: _.findWhere(ctrl.collectors, {collectorType: 'Cloud'}).id,
                options: {
                    accountNumber: accountNumber,
                    tagName: tagName,
                    tagValue: tagValue
                }
            };

            return collectorData.createCollectorItem(item);
        }

        function passDataToView() {

            var postObj = {
                name: 'cloud',
                options: {
                    id: widgetConfig.options.id,
                    accountNumber: ctrl.accountNumber,
                    tagName: ctrl.tagName,
                    tagValue: ctrl.tagValue
                },
                componentId: modalData.dashboard.application.components[0].id
            };

            // pass this new config to the modal closing so it's saved
            $modalInstance.close(postObj);
        }

        function processCollectorsResponse(data) {
            ctrl.collectors = data;
        }


        // Request collecters
        collectorData.collectorsByType('Cloud').then(processCollectorsResponse);


        // public properties/methods
        ctrl.accountNumber = undefined;
        ctrl.tagName = undefined;
        ctrl.tagValue = undefined;
        ctrl.collectors = [];

        // public methods
        ctrl.submit = function (valid) {
            if (valid) {
                createCloudConfigItem(ctrl.accountNumber, ctrl.tagName, ctrl.tagValue).
                then(passDataToView());
            }
        }

    }
})();
