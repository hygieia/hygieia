(function () {
    'use strict';

    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'Cloud' // widget title
                },
                controller: 'cloudViewController',
                controllerAs: 'cloudView', // defaults to ctrl
                templateUrl: 'components/widgets/cloud/view.html'
            },
            config: {
                controller: 'cloudConfigController',
                controllerAs: 'cloudConfig', // defaults to ctrl
                templateUrl: 'components/widgets/cloud/config.html'
            },
            getState: getState
        };

    angular
        .module('devops-dashboard')
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WIDGET_STATE'];
    function register(widgetManagerProvider, WIDGET_STATE) {
        widget_state = WIDGET_STATE;
        widgetManagerProvider.register('cloud', config);
    }

    // implement custom logic for determining the widget state in this method
    function getState(widgetConfig) {
        return localTesting || widgetConfig.id ?
            widget_state.READY :
            widget_state.CONFIGURE;//change ready to CONFIG
    }
})();
