(function () {
    'use strict';

    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'Monitor' // widget title
                },
                controller: 'monitorViewController',
                controllerAs: 'ctrl',
                templateUrl: 'components/widgets/monitor/view.html'
            },
            config: {
                controller: 'monitorConfigController',
                controllerAs: 'ctrl',
                templateUrl: 'components/widgets/monitor/config.html'
            },
            getState: getState
        };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('monitor', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local ?
            widget_state.READY :
            (widgetConfig.id ? widget_state.READY : widget_state.CONFIGURE);
    }
})();
