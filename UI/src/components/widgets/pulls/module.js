(function () {
    'use strict';

    var widget_state,
        config = {
        view: {
            defaults: {
                title: 'Pulls Data' // widget title
            },
            controller: 'PullViewController',
            controllerAs: 'pullView',
            templateUrl: 'components/widgets/pulls/view.html'
        },
        config: {
            controller: 'PullConfigController',
            controllerAs: 'pullConfig',
            templateUrl: 'components/widgets/pulls/config.html'
        },
        getState: getState
    };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('pulls', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local || (widgetConfig.id) ? widget_state.READY : widget_state.CONFIGURE;
    }
})();
