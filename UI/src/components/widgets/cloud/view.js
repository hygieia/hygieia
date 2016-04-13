/**
 * Created by hyw912 on 4/12/16.
 */


/**
 * View controller for the build widget
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('CloudWidgetViewController', CloudWidgetViewController);

    CloudWidgetViewController.$inject = ['$scope', '$modal', 'cloudData'];

    function CloudWidgetViewController($scope, $modal, cloudData) {

        var ctrl = this;
        ctrl.load = function() {
            return cloudData.getData();
        }
    }
})();