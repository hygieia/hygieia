(function() {
    'use strict';
    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'Team' // widget title
                },
                controller: 'TeamViewController',
                controllerAs: 'teamView',
                templateUrl: 'components/widgets/team/view.html'
            },
            config: {
              controller: 'TeamWidgetConfigController',
                controllerAs: 'teamConfig',
                templateUrl: 'components/widgets/team/config.html'
            },
            getState: getState
        };
    angular
        .module(HygieiaConfig.module)
        .config(register);
    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('team', config);
    }
    function getState(widgetConfig) {
        return HygieiaConfig.local || (widgetConfig.id) ? widget_state.READY : widget_state.CONFIGURE;
    }
})();
