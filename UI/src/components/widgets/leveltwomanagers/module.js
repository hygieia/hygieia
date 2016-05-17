(function () {
    'use strict';

    var widget_state,
        config = {
        view: {
            defaults: {
                title: 'Leveltwomanagers Data' // widget title
            },
            controller: 'LeveltwomanagerViewController',
            controllerAs: 'leveltwomanagerView',
            templateUrl: 'components/widgets/leveltwomanagers/view.html'
        },
        config: {
            controller: 'LeveltwomanagerConfigController',
            controllerAs: 'leveltwomanagerConfig',
            templateUrl: 'components/widgets/leveltwomanagers/config.html'
        },
        getState: getState
    };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('leveltwomanagers', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local || (widgetConfig.id) ? widget_state.READY : widget_state.CONFIGURE;
    }
})();
