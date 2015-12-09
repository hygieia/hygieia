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
        .module('devops-dashboard')
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WIDGET_STATE'];
    function register(widgetManagerProvider, WIDGET_STATE) {
        widget_state = WIDGET_STATE;
        widgetManagerProvider.register('chatops', config);
    }

    function getState(widgetConfig) {
        return localTesting || (widgetConfig.id) ? widget_state.READY : widget_state.CONFIGURE;
    }
})();
