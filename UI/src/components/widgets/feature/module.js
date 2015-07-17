(function () {
    'use strict';

    var widget_state,
        config = {
        view: {
            defaults: {
                title: 'feature' // widget title
            },
            controller: 'featureViewController',
            //controllerAs: 'feature',
            templateUrl: 'components/widgets/feature/view.html'
        },
        config: {
            controller: 'featureConfigController',
            templateUrl: 'components/widgets/feature/config.html'
        },
        getState: getState
    };

    angular
        .module('devops-dashboard')
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WIDGET_STATE'];
    function register(widgetManagerProvider, WIDGET_STATE) {
        widget_state = WIDGET_STATE;
        widgetManagerProvider.register('feature', config);
    }

    function getState(widgetConfig) {
        //return widget_state.READY;
        return localTesting || widgetConfig.id ?
                widget_state.READY :
                widget_state.CONFIGURE;
    }
})();
