(function () {
    'use strict';

    var widget_state,
        config = {
        view: {
            defaults: {
                title: 'Performance' // widget title
            },
            controller: 'performanceViewController', //look at view.js
            controllerAs: 'performanceView',
            templateUrl: 'components/widgets/performance/view.html'
        },
        config: { //look at config.js
            controller: 'performanceConfigController',
            controllerAs: 'performanceConfig',
            templateUrl: 'components/widgets/performance/config.html'
        },
        getState: getState
    };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('performance', config);
    }

    function getState(widgetConfig) {
        return widget_state.READY;
        //return HygieiaConfig.local || (widgetConfig.id) ? widget_state.READY : widget_state.CONFIGURE;
    }
})();
