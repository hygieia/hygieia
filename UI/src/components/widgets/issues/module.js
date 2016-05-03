(function () {
    'use strict';

    var widget_state,
        config = {
        view: {
            defaults: {
                title: 'Issues Data' // widget title
            },
            controller: 'IssueViewController',
            controllerAs: 'issueView',
            templateUrl: 'components/widgets/issues/view.html'
        },
        config: {
            controller: 'IssueConfigController',
            controllerAs: 'issueConfig',
            templateUrl: 'components/widgets/issues/config.html'
        },
        getState: getState
    };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('issues', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local || (widgetConfig.id) ? widget_state.READY : widget_state.CONFIGURE;
    }
})();
