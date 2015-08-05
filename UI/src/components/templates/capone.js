/**
 * Controller for the dashboard route.
 * Render proper template.
 */
(function () {
    'use strict';

    angular
        .module('devops-dashboard')
        .controller('CapOneTemplateController', CapOneTemplateController);

    CapOneTemplateController.$inject = [];
    function CapOneTemplateController() {
        var ctrl = this;

        ctrl.widgetView = true;
        ctrl.toggleView = function () {
            ctrl.widgetView = !ctrl.widgetView;
        };

        ctrl.hasComponents = function (dashboard, names) {
            var hasAllComponents = true;

            try {
                _(names).forEach(function (name) {
                    if(!dashboard.application.components[0].collectorItems[name]) {
                        hasAllComponents = false;
                    }
                });
            } catch(e) {
                hasAllComponents = false;
            }

            return hasAllComponents;
        };
    }
})();