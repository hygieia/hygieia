(function () {
    'use strict';

    var widget_state,
        config = {
        view: {
            defaults: {
                title: 'Levelonemanagers Data' // widget title
            },
            controller: 'LevelonemanagerViewController',
            controllerAs: 'levelonemanagerView',
            templateUrl: 'components/widgets/levelonemanagers/view.html'
        },
        config: {
            controller: 'LevelonemanagerConfigController',
            controllerAs: 'levelonemanagerConfig',
            templateUrl: 'components/widgets/levelonemanagers/config.html'
        },
        getState: getState
    };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('levelonemanagers', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local || (widgetConfig.id) ? widget_state.READY : widget_state.CONFIGURE;
    }
})();
