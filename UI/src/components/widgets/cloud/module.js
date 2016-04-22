
(function () {
    'use strict';

    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'Cloud' // widget title
                },
                controller: 'CloudWidgetViewController',
                controllerAs: 'cloudView',
                templateUrl: 'components/widgets/cloud/view.html'
            },
            config: {
                controller: 'CloudWidgetConfigController',
                controllerAs: 'cloudConfig',
                templateUrl: 'components/widgets/cloud/config.html'
            },
            getState: getState,
            collectors: ['cloud']
        };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('cloud', config);
    }

    function getState(config) {
        return HygieiaConfig.local ?
=======
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
>>>>>>> upstream/hygieiacloud2.1
            widget_state.READY :
            widget_state.CONFIGURE;
    }
})();
<<<<<<< HEAD

=======
>>>>>>> upstream/hygieiacloud2.1
