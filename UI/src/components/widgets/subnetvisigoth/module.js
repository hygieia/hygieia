(function () {
    'use strict';

    var widget_state,
        config = {
        view: {
            defaults: {
                title: 'Subnet Visigoth' // widget title
            },
            controller: 'SubnetVisigothViewController',
            controllerAs: 'subnetVisigothView',
            templateUrl: 'components/widgets/subnetvisigoth/view.html'
        },
        config: {
            controller: 'SubnetVisigothConfigController',
            controllerAs: 'subnetVisigothConfig',
            templateUrl: 'components/widgets/subnetvisigoth/config.html'
        },
        getState: getState
    };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('subnetvisigoth', config);
    }

    function getState(widgetConfig) {
      //  return HygieiaConfig.local || (widgetConfig.id) ? widget_state.READY : widget_state.CONFIGURE;
      return widget_state.READY;
    }
})();
