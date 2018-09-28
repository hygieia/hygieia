(function () {
    'use strict';

    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'Log' // widget title
                },
                controller: 'LogViewController',
                controllerAs: 'ctrl',
                templateUrl: 'components/widgets/log/view.html'
            },
            config: {
                controller: 'LogConfigController',
                controllerAs: 'logConfig',
                templateUrl: 'components/widgets/log/config.html'
            },
            getState: getState,
            collectors: ['log']
        };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('log', config);
    }

    function getState(config) {
        return HygieiaConfig.local || (config.id) ? widget_state.READY : widget_state.CONFIGURE;
    }
})();
