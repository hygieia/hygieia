(function () {
    'use strict';

    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'Build' // widget title
                },
                controller: 'BuildWidgetViewController',
                controllerAs: 'buildView',
                templateUrl: 'components/widgets/build/view.html'
            },
            config: {
                controller: 'BuildWidgetConfigController',
                controllerAs: 'buildConfig',
                templateUrl: 'components/widgets/build/config.html'
            },
            getState: getState,
            collectors: ['build']
        };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('build', config);
    }

    function getState(config) {
        // make sure config values are set
        return HygieiaConfig.local || (config.id && config.options.buildDurationThreshold && config.options.consecutiveFailureThreshold) ?
            widget_state.READY :
            widget_state.CONFIGURE;
    }
})();
