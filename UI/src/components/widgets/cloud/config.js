(function () {
    'use strict';

    angular
        .module('devops-dashboard')
        .controller('cloudConfigController', cloudConfigController);

    cloudConfigController.$inject = ['$scope', 'modalData', 'cloudData', '$modalInstance', 'collectorData'];
    function cloudConfigController($scope, modalData, cloudData, $modalInstance, collectorData) {
        var ctrl = this;


        ctrl.services = [{'name': 'AWS'}];
        ctrl.toolsDropdownDisabled = true;
        ctrl.toolsDropdownPlaceholder = 'Select a service';
        ctrl.submitted = false;
        ctrl.validAccess = false;

        var widgetConfig = modalData.widgetConfig;

        // public methods
        ctrl.showError = showError;
        ctrl.submit = submitConfig;

        console.log("WidgetConfig:", widgetConfig);
        console.log("Ctrl: ", ctrl);
        // Request collectors
        collectorData.collectorsByType('cloud').then(processCollectorsResponse);

        var idx;

        for (var v = 0; v < ctrl.services.length; v++) {
            if (ctrl.services[v].name == widgetConfig.options.cloudProvider) {
                console.log("Matched with :", widgetConfig.options.cloudProvider);
                //console.log("Selected Index =", ctrl.services.selectedIndex);
                idx = v;
                ctrl.service = ctrl.services[idx].name;
                ctrl.services.selectedIndex = idx;

            }
        }
        ctrl.service = "AWS";
        ctrl.accessKey = widgetConfig.options.accessKey;
        ctrl.secretKey = widgetConfig.options.secretKey;




        function processCollectorsResponse(data) {
            console.log(data);
            ctrl.collectors = data;
        }

        function showError(element) {
            // tell the view whether or not to show errors only once the form has been submitted once
            return element.$invalid && ctrl.submitted;
        }

        function submitConfig(valid, cloudProvider) {
            ctrl.submitted = true;

            if (valid) {
                //make get request to validate access and secret key
                var item = {};

                item = {
                    collectorId: _.findWhere(ctrl.collectors, {name: 'AWSCloud'}).id,
                    options: {
                        accessKey: ctrl.accessKey,
                        secretKey: ctrl.secretKey,
                        cloudProvider: ctrl.service
                    }
                };
                console.log(item);
                cloudData.saveConfig(item).then(processCollectorItemResponse);
            }
        }


        function processCollectorItemResponse(response) {
            ctrl.validAccess = true;
            console.log(response);
            var postObj = {
                name: "cloud",
                options: {
                    id: widgetConfig.options.id,
                    accessKey: response.options.accessKey,
                    secretKey: response.options.secretKey,
                    cloudProvider: ctrl.service
                },
                componentId: modalData.dashboard.application.components[0].id,
                collectorItemId: response.id
            };

            // pass this new config to the modal closing so it's saved
            $modalInstance.close(postObj);
        }
    }
})();
