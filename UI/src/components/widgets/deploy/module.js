(function () {
    'use strict';

    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'Deploy' // widget title
                },
                controller: 'deployViewController',
                controllerAs: 'deployView',
                templateUrl: 'components/widgets/deploy/view.html'
            },
            config: {
                controller: 'deployConfigController',
                controllerAs: 'deployConfig',
                templateUrl: 'components/widgets/deploy/config.html'
            },
            getState: getState
        };

    angular
        .module('devops-dashboard')
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('deploy', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.localTesting || widgetConfig.id ?
            widget_state.READY :
            widget_state.CONFIGURE;
    }
})();
