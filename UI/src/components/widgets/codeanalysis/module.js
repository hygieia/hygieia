(function () {
    'use strict';

    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'Quality' // widget title
                },
                controller: 'CodeAnalysisViewController',
                controllerAs: 'caWidget',
                templateUrl: 'components/widgets/codeanalysis/view.html'
            },
            config: {
                controller: 'CodeAnalysisConfigController',
                controllerAs: 'caWidget',
                templateUrl: 'components/widgets/codeanalysis/config.html'
            },
            getState: getState,
            collectors: ['codequality']
        };

    angular
        .module('devops-dashboard')
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WIDGET_STATE'];
    function register(widgetManagerProvider, WIDGET_STATE) {
        widget_state = WIDGET_STATE;
        widgetManagerProvider.register('codeanalysis', config);
    }

    function getState(widgetConfig) {
        // make sure config values are set
        return localTesting || (widgetConfig.id) ? widget_state.READY : widget_state.CONFIGURE;
    }
})();
