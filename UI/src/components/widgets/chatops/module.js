(function () {
    'use strict';

    var widget_state,
        config = {
        view: {
            defaults: {
                title: 'ChatOps' // widget title
            },
            controller: 'ChatOpsViewController',
            controllerAs: 'chatOpsView',
            templateUrl: 'components/widgets/chatops/view.html'
        },
        config: {
            controller: 'ChatOpsConfigController',
            controllerAs: 'chatOpsConfig',
            templateUrl: 'components/widgets/chatops/config.html'
        },
        getState: getState,
            collectors: ['chatops']
    };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('chatops', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local || (widgetConfig.id) ? widget_state.READY : widget_state.CONFIGURE;
    }
})();
