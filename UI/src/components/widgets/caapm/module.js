(function () {
    'use strict';

    var widget_state,
        config = {
             view: {
            defaults: {
                title: 'Ca - APM' 
            },
            controller: 'CaapmViewController', 
            controllerAs: 'caApmVConfig',
            templateUrl: 'components/widgets/caapm/view.html'
        },
        config: {
            controller: 'CaapmConfigController',
            controllerAs: 'caApmConfig',
            templateUrl: 'components/widgets/caapm/config.html'
        },
        getState: getState,
            collectors: ['caapm']
        };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('caapm', config);
    }

    function getState(config) {
        // make sure config values are set
        return HygieiaConfig.local || (config.id) ? widget_state.READY : widget_state.CONFIGURE;
    }
})();