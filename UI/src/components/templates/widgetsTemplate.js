/**
 * Controller for the Widget Managed template.
 *
 */
(function () {
    'use strict';
    angular
        .module(HygieiaConfig.module)
        .controller('WidgetTemplateController', WidgetTemplateController);

    WidgetTemplateController.$inject = ['$scope'];
    function WidgetTemplateController($scope) {
        var ctrl = this;
        ctrl.tabs = [
            {name: "Widget"},
            {name: "Pipeline"},
            {name: "Cloud"}
        ];
        ctrl.pipelineInd = false;
        ctrl.cloudInd = false;
        ctrl.widgetView = ctrl.tabs[0].name;
        ctrl.toggleView = function (index) {
            ctrl.widgetView = typeof ctrl.tabs[index] === 'undefined' ? ctrl.tabs[0].name : ctrl.tabs[index].name;
        };

        $scope.init = function (dashboard) {
            ctrl.sortOrder = [];
            var widgetObj = {};
            ctrl.widgets = dashboard.activeWidgets;
            _(ctrl.widgets).forEach(function (widget) {
                if (widget == 'pipeline') {
                    ctrl.pipelineInd = true;
                } else if (widget == 'cloud') {
                    ctrl.cloudInd = true;
                } else {
                    if (widget == 'codeanalysis') {
                        widgetObj[widget] = 'Code Analysis';
                    } else if (widget === 'performance') {
                        widgetObj[widget] = 'Performance Analysis';
                    } else {
                        widgetObj[widget] = getDisplayName(widget);
                    }
                }

            });
            ctrl.widgetDisplay = widgetObj;
            _.each(ctrl.widgetDisplay, function (val, key) {
                ctrl.sortOrder.push(key);
            });
            ctrl.widgetsOrder = chunk(ctrl.sortOrder, 3);
            if (ctrl.pipelineInd === false) {
                for (var i = 0; i < ctrl.tabs.length; i++)
                    if (ctrl.tabs[i].name === "Pipeline") {
                        ctrl.tabs.splice(i, 1);
                        break;
                    }
            }
            if (ctrl.cloudInd === false) {
                for (var i = 0; i < ctrl.tabs.length; i++)
                    if (ctrl.tabs[i].name === "Cloud") {
                        ctrl.tabs.splice(i, 1);
                        break;
                    }
            }
        };

        // break array into chunk of 3
        function chunk(arr, chunkSize) {
            var returnArray = [];
            for (var i = 0, len = arr.length; i < len; i += chunkSize)
                returnArray.push(arr.slice(i, i + chunkSize));
            return returnArray;
        }

        //get display name in camel case
        function getDisplayName(title) {
            return title.charAt(0).toUpperCase() + title.slice(1);
        }
    }
})();



