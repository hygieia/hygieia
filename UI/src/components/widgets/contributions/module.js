(function () {
    'use strict';

    var widget_state,
        config = {
        view: {
            defaults: {
                title: 'contributions Repo' // widget title
            },
            controller: 'RepoViewController',
            controllerAs: 'repoView',
            templateUrl: 'components/widgets/contributions/view.html'
        },
        config: {
            controller: 'RepoConfigController',
            controllerAs: 'contributionsConfig',
            templateUrl: 'components/widgets/contributions/config.html'
        },
        getState: getState
    };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('contributions', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local || (widgetConfig.id) ? widget_state.READY : widget_state.CONFIGURE;
    }
})();
