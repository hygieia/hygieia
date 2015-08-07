(function () {
    'use strict';

    angular
        .module('devops-dashboard')
        .controller('cloudConfigController', cloudConfigController);

    cloudConfigController.$inject = ['modalData', 'cloudData', '$modalInstance'];
    function cloudConfigController(modalData, cloudData, $modalInstance) {
        var ctrl = this;


        ctrl.services = [{'name': 'Amazon Web Services'}];
        ctrl.toolsDropdownDisabled = true;
        ctrl.toolsDropdownPlaceholder = 'Select a service';
        ctrl.submitted = false;
        ctrl.validAccess = false;


        // public methods
        ctrl.showError = showError;
        ctrl.submit = submitConfig;


        function showError(element) {
            // tell the view whether or not to show errors only once the form has been submitted once
            return element.$invalid && ctrl.submitted;
        }

        function submitConfig(valid) {
            ctrl.submitted = true;

            if (valid) {
                //make get request to validate access and secret key
                var params = {'accessKey': ctrl.accessKey};
                cloudData.accessAuthentication(params).then(function (data) {
                    if (data === true) {
                        ctrl.validAccess = true;
                    }
                    modalData.widgetConfig.name = "cloud";
                    modalData.widgetConfig.options.accessKey = ctrl.accessKey;
                    modalData.widgetConfig.options.secretKey = ctrl.secretKey;
                    modalData.widgetConfig.componentId = modalData.dashboard.application.components[0].id;

                }).catch(function (err) {
                });


                // by passing an object back while closing the modal the base widget classes
                // will save it to the api and reload the widget
                $modalInstance.close(modalData.widgetConfig);
            }
        }

        function createCollectorItem(data) {
            //TODO: a collector item needs to be created to follow the same format as other widget
            //var item = {collectorId: ,
            //name:
            //options};
            return collectorData.createCollectorItem(item);
        }

        function createCollectorItem(data) {
            var item = {
                // TODO - Remove hard-coded versionone reference when mulitple
                // scm collectors become available
                collectorId: _.findWhere(ctrl.collectors, {
                    name: "VersionOne"
                }).id,
                options: {
                    data: data
                }
            };
            return collectorData.createCollectorItem(item);
        }

    }
})();
