/**
 * Empty widget container message pane
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .directive('emptyWidgetContainer', function () {
            return {
                template: '<div class="empty-container-banner">' +
                'No widgets have been activated for this portion of the dashboard. Please see dashboard Owner or Administrator for activation of widgets.</div>'
            };
        });
})();