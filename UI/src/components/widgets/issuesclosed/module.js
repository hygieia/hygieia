(function () {
    'use strict';

    var widget_state,
        config = {
        view: {
            defaults: {
                title: 'Issues Data' // widget title
            },
            controller: 'IssueClosedViewController',
            controllerAs: 'issueClosedView',
            templateUrl: 'components/widgets/issuesclosed/view.html'
        },
        config: {
            controller: 'IssueClosedConfigController',
            controllerAs: 'issueClosedConfig',
            templateUrl: 'components/widgets/issuesclosed/config.html'
        },
        getState: getState
    };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('issuesclosed', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local || (widgetConfig.id) ? widget_state.READY : widget_state.CONFIGURE;
    }
})();
