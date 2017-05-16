(function () {
    'use strict';

    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'WebLogic Monitor' // widget title
                },
                controller: 'wlmonitorViewController',
                controllerAs: 'wlMonitorView',
                templateUrl: 'components/widgets/wlmonitor/view.html'
            },
            config: {
                controller: 'wlmonitorConfigController',
                controllerAs: 'wlMonitorConfig',
                templateUrl: 'components/widgets/wlmonitor/config.html'
            },
            getState: getState
        };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('wlmonitor', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local ?
            widget_state.READY :
            (widgetConfig.id ? widget_state.READY : widget_state.CONFIGURE); //CONFIGURE
    }
})();
