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
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('repo', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local || (widgetConfig.id) ? widget_state.READY : widget_state.CONFIGURE;
    }
})();
