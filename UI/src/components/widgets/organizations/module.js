(function () {
    'use strict';

    var widget_state,
        config = {
        view: {
            defaults: {
                title: 'Organizations Data' // widget title
            },
            controller: 'OrganizationViewController',
            controllerAs: 'organizationView',
            templateUrl: 'components/widgets/organizations/view.html'
        },
        config: {
            controller: 'OrganizationConfigController',
            controllerAs: 'organizationConfig',
            templateUrl: 'components/widgets/organizations/config.html'
        },
        getState: getState
    };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('organizations', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local || (widgetConfig.id) ? widget_state.READY : widget_state.CONFIGURE;
    }
})();
