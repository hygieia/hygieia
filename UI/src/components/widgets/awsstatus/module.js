(function () {
    'use strict';

    var widget_state;
    var config = {
            view: {
                defaults: {
                    title: 'AWS Status' // widget title
                },
                controller: 'awsStatusViewController',
                controllerAs: 'ctrl',
                templateUrl: 'components/widgets/awsstatus/view.html'
            },
            config: {
                controller: 'awsStatusConfigController',
                controllerAs: 'ctrl',
                templateUrl: 'components/widgets/awsstatus/config.html'
            },
            getState: getState
        };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('awsstatus', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local ?
            widget_state.READY :
            (widgetConfig.id ? widget_state.READY : widget_state.CONFIGURE);
    }
})();