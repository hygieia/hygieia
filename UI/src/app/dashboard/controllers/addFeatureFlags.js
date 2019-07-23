(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('AddFeatureFlagController', AddFeatureFlagController);

    AddFeatureFlagController.$inject = ['$uibModalInstance', 'featureFlagsData','$sce'];

    function AddFeatureFlagController($uibModalInstance, featureFlagsData,$sce) {
        var ctrl = this;
        ctrl.configTooltip = $sce.trustAsHtml("<div class='tooltipList'>{\"name\": \"name\", \"description\":\"Default description\", \"flags\": {}}</div>");
        ctrl.placeHolder= '{"name": "name", "description":"Default description", "flags": {}}';

        // public methods
        ctrl.submit = submit;

        function submit(form) {
            if (form.$valid) {
                console.log('val is ' + document.cdf.featureflags.value);
               var featureFlags ={
                   "json":document.cdf.featureflags.value
               }
                featureFlagsData
                    .createOrUpdateFeatureFlags(featureFlags)
                    .success(function (response) {
                        $uibModalInstance.close();
                    })
                    .error(function (response) {
                        console.log(response);

                    });
            }
        }
    }
})();
