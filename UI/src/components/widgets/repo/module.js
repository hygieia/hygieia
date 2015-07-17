(function () {
    'use strict';

    var widget_state,
        config = {
        view: {
            defaults: {
                title: 'Code Repo' // widget title
            },
            controller: 'RepoViewController',
            controllerAs: 'repoView',
            templateUrl: 'components/widgets/repo/view.html'
        },
        config: {
            controller: 'RepoConfigController',
            controllerAs: 'repoConfig',
            templateUrl: 'components/widgets/repo/config.html'
        },
        getState: getState
    };

    angular
        .module('devops-dashboard')
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WIDGET_STATE'];
    function register(widgetManagerProvider, WIDGET_STATE) {
        widget_state = WIDGET_STATE;
        widgetManagerProvider.register('repo', config);
    }

    function getState(widgetConfig) {
        return localTesting || (widgetConfig.id) ? widget_state.READY : widget_state.CONFIGURE;
    }
})();
