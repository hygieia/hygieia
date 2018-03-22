/**
 * Controller for the Custom template.
 *
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('CustomTemplateController', CapOneTemplateController)
        .filter( 'camelCase', function ()
        {
            var camelCaseFilter = function ( input )
            {
                var charZero = input.charAt(0);
                var upperc = charZero.toUpperCase();
                var slice = input.slice(1);
                var joined = upperc + slice;
                return  joined;
            };
            return camelCaseFilter;
        });


    CapOneTemplateController.$inject = ['$scope','templateMangerData'];
    function CapOneTemplateController($scope,templateMangerData) {
        var ctrl = this;

        ctrl.tabs = [
            {name: "Widget"},
            {name: "Pipeline"},
            {name: "Cloud"}
        ];


        ctrl.widgetView = ctrl.tabs[0].name;
        ctrl.toggleView = function (index) {
            ctrl.widgetView = typeof ctrl.tabs[index] === 'undefined' ? ctrl.tabs[0].name : ctrl.tabs[index].name;
        };

        $scope.init = function (dashboard) {
            var dash = dashboard;

            templateMangerData.search(dashboard.template).then(function (response) {
                var result = response;
                var widgetObj = {};
                ctrl.widgets = response.widgets;
                _(ctrl.widgets).forEach(function (widget) {
                    if(widget=='codeanalysis'){
                        widgetObj[widget] = 'Code Analysis';
                    }else if(widget==='performance'){
                        widgetObj[widget]='Performance Analysis';
                    }else{
                        widgetObj[widget]= getDisplayName(widget);
                    }
                });
                ctrl.widgetDisplay = widgetObj;
                ctrl.sortOrder = response.order;
                //Check in parent controller if score is enabled
                //Push to the top of display
                if ($scope.ctrl.scoreWidgetEnabled) {
                    ctrl.sortOrder.unshift('score');
                }
                ctrl.widgetsOrder = chunk(ctrl.sortOrder,3);
            });
        };

        // private methods

        // break array into chunk of 3
        function chunk(arr, chunkSize) {
            var returnArray = [];
            for (var i=0,len=arr.length; i<len; i+=chunkSize)
                returnArray.push(arr.slice(i,i+chunkSize));
            return returnArray;
        }

        //get display name in camel case
        function  getDisplayName(title) {
            return title.charAt(0).toUpperCase()+title.slice(1);
        }
    }

})();



