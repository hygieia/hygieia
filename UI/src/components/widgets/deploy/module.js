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

    register.$inject = ['widgetManagerProvider', 'WIDGET_STATE'];
    function register(widgetManagerProvider, WIDGET_STATE) {
        widget_state = WIDGET_STATE;
        widgetManagerProvider.register('deploy', config);
    }

    function getState(widgetConfig) {
        return localTesting || widgetConfig.id ?
            widget_state.READY :
            widget_state.CONFIGURE;
    }
})();
