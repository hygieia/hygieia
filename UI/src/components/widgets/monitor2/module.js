(function () {
    'use strict';

    var widget_state;
    var config = {
            view: {
                defaults: {
                    title: 'Monitor2' // widget title
                },
                controller: 'monitor2ViewController',
                controllerAs: 'ctrl',
                templateUrl: 'components/widgets/monitor2/view.html'
            },
            config: {
                controller: 'monitor2ConfigController',
                controllerAs: 'ctrl',
                templateUrl: 'components/widgets/monitor2/config.html'
            },
            getState: getState
        };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('monitor2', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local ?
            widget_state.READY :
            (widgetConfig.id ? widget_state.READY : widget_state.CONFIGURE);
    }
})();