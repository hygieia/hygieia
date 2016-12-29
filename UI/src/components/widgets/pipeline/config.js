(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('pipelineConfigController', pipelineConfigController);

    pipelineConfigController.$inject = ['modalData', 'deployData', '$modalInstance'];
    function pipelineConfigController(modalData, deployData, $modalInstance) {
        /*jshint validthis:true */
        var ctrl = this;

        // make sure mappings property is available
        ctrl.environmentsDropdownDisabled = true;
        ctrl.environmentMappings = [
            { key: 'dev', value: null },
            { key: 'qa', value: null },
            { key: 'int', value: null },
            { key: 'perf', value: null },
            { key: 'prod', value: null }
        ];

        if(modalData.widgetConfig.options.mappings) {
            _(ctrl.environmentMappings).forEach(function(env) {
                if(modalData.widgetConfig.options.mappings[env.key]) {
                    env.value = modalData.widgetConfig.options.mappings[env.key];
                }
            });
        }

        ctrl.save = save;

        deployData.details(modalData.dashboard.application.components[0].id).then(processResponse);


        function processResponse(data) {
            ctrl.environments = _(data.result).map(function (env) {
                return {
                    name: env.name,
                    value: env.name.toLowerCase()
                };
            }).value();

            ctrl.mappings = {};

            for(var x in modalData.widgetConfig.options.mappings) {
                var envName = modalData.widgetConfig.options.mappings[x];
                if(_(ctrl.environments).where({'value':envName}).value().length) {
                    ctrl.mappings[x] = envName;
                }
            }


            ctrl.environmentsDropdownDisabled = false;
        }

        function save() {
            modalData.widgetConfig.name = 'pipeline';
            modalData.widgetConfig.options.mappings = ctrl.mappings;

            var postObj = angular.copy(modalData.widgetConfig);
            $modalInstance.close(postObj);
        }
    }
})();
