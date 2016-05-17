(function () {
    'use strict';

    var widget_state,
        config = {
        view: {
            defaults: {
                title: 'Pulls Merged Data' // widget title
            },
            controller: 'PullMergedViewController',
            controllerAs: 'pullMergedView',
            templateUrl: 'components/widgets/pullsmerged/view.html'
        },
        config: {
            controller: 'PullMergedConfigController',
            controllerAs: 'pullMergedConfig',
            templateUrl: 'components/widgets/pullsmerged/config.html'
        },
        getState: getState
    };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('pullsmerged', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local || (widgetConfig.id) ? widget_state.READY : widget_state.CONFIGURE;
    }
})();
