(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('pipelineConfigController', pipelineConfigController);

    pipelineConfigController.$inject = ['modalData', 'deployData', 'systemConfigData', '$modalInstance', '$q'];
    function pipelineConfigController(modalData, deployData, systemConfigData, $modalInstance, $q) {
        /*jshint validthis:true */
        var ctrl = this;

        // make sure mappings property is available
        ctrl.environmentsDropdownDisabled = true;
        ctrl.environmentMappings = [ ];

        ctrl.save = save;

        $q.all([systemConfigData.config(), deployData.details(modalData.dashboard.application.components[0].id)]).then(processResponse);

        function processResponse(dataA) {
        	var systemConfig = dataA[0];
        	var data = dataA[1];
        	
        	ctrl.environmentMappings = _(systemConfig.systemStages)
        		.filter(function (stage) { return stage.type == 'DEPLOY' })
	        	.map(function (stage) {
	        		return { key: stage.name.toLowerCase(), value: null }
	        	}).value();
        	
            if(modalData.widgetConfig.options.mappings) {
                _(ctrl.environmentMappings).forEach(function(env) {
                    if(modalData.widgetConfig.options.mappings[env.key]) {
                        env.value = modalData.widgetConfig.options.mappings[env.key];
                    }
                });
            }
        	
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
