/**
 * TODO: Not Implemented
 *
 * The idea behind the widget-placeholder directive is that it could be
 * added inside a widget-container in the template file to dynamically control
 * the ability to add or manage widgets
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .directive('widgetPlaceholder', WidgetPlaceholder);

    WidgetPlaceholder.$inject = [];
    function WidgetPlaceholder() {
        return {
            require: '^widgetContainer',
            restrict: 'E',
            link: link
        };

        function link(scope, element, attrs, containerCtrl) {
            containerCtrl.registerPlaceholder({
                element: element,
                attrs: attrs
            });
        }
    }
})();