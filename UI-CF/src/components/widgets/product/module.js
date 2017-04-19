(function () {
    'use strict';

    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'Product' // widget title
                },
                controller: 'productViewController',
                controllerAs: 'ctrl',
                templateUrl: 'components/widgets/product/view.html'
            },
            getState: function() {
                return widget_state.READY;
            }
        };

    angular
        .module(HygieiaConfig.module)
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('product', config);
    }
})();
