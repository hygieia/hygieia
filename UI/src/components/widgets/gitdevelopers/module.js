(function () {
    'use strict';

    var widget_state,
        config = {
        view: {
            defaults: {
                title: 'Gitdevelopers Data' // widget title
            },
            controller: 'GitdeveloperViewController',
            controllerAs: 'gitdeveloperView',
            templateUrl: 'components/widgets/gitdevelopers/view.html'
        },
        config: {
            controller: 'GitdeveloperConfigController',
            controllerAs: 'gitdeveloperConfig',
            templateUrl: 'components/widgets/gitdevelopers/config.html'
        },
        getState: getState
    };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('gitdevelopers', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local || (widgetConfig.id) ? widget_state.READY : widget_state.CONFIGURE;
    }
})();
