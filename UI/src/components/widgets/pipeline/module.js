(function () {
    'use strict';

    var widget_state,
        config = {
        view: {
            defaults: {
                title: 'pipeline' // widget title
            },
            controller: 'pipelineViewController',
            controllerAs: 'pipelineView',
            templateUrl: 'components/widgets/pipeline/view.html'
        },
        config: {
            controller: 'pipelineConfigController',
            controllerAs: 'pipelineConfig',
            templateUrl: 'components/widgets/pipeline/config.html'
        },
        getState: getState
    };

    angular
        .module('devops-dashboard')
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WIDGET_STATE'];
    function register(widgetManagerProvider, WIDGET_STATE) {
        widget_state = WIDGET_STATE;
        widgetManagerProvider.register('pipeline', config);
    }

    function getState(widgetConfig) {
        if(widgetConfig.options && widgetConfig.options.mappings) {
            var ready = false;
            _(widgetConfig.options.mappings).forEach(function(value) {
                if(value) {
                    ready = true;
                }
            });

            if(ready) {
                return widget_state.READY;
            }
        }

        return widget_state.CONFIGURE;
    }
})();
