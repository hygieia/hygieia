/**
 * Controller for the template details
 */
(function () {
    'use strict';

    angular.module(HygieiaConfig.module)
        .controller('TemplateDetailsController', TemplateDetailsController);

    TemplateDetailsController.$inject = ['modalData'];
    function TemplateDetailsController(modalData) {
        var ctrl = this;
        ctrl.templateDetails = modalData.templateDetails;
    }
})();