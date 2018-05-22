(function() {
    'use strict';
    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'Rally' // widget title
                },
                controller: 'RallyViewController',
                controllerAs: 'rallyView',
                templateUrl: 'components/widgets/rally/view.html'
            },
            config: {
              controller: 'RallyWidgetConfigController',
                controllerAs: 'rallyConfig',
                templateUrl: 'components/widgets/rally/config.html'
            },
            getState: getState
        };
    angular
        .module(HygieiaConfig.module)
        .config(register);
    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('rally', config);
    }
    function getState(widgetConfig) {
        return HygieiaConfig.local || (widgetConfig.id) ? widget_state.READY : widget_state.CONFIGURE;
    }
})();
