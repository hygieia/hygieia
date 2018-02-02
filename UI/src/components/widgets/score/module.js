(function () {
    'use strict';

    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'Score' // widget title
                },
                controller: 'scoreViewController',
                controllerAs: 'scoreView',
                templateUrl: 'components/widgets/score/view.html'
            },
            config: {
                controller: 'scoreConfigController',
                controllerAs: 'scoreConfig',
                templateUrl: 'components/widgets/score/config.html'
            },
            getState: getState
        };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('score', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local || widgetConfig.id ?
            widget_state.READY :
            widget_state.CONFIGURE;
    }
})();
