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
        //To add more panels, expand widgetView array and views array
        ctrl.widgetView = [0, 0, 1];
        ctrl.toggleView = function (clickEvent) {
            var views = ["widget", "pipeline", "cloud"];
            var count = 0;
            //make sure that the target is not icon
            while(views.indexOf(clickEvent.path[count].id)<0 && count<=clickEvent.path.length)
                count++;
            //see which button is being clicked and apply new array accordingly
            for(var i = 0 ; i < views.length;i++){
                if(clickEvent.path[count].id === views[i]){
                    var array = Array.apply(null, new Array(views.length)).map(Number.prototype.valueOf,0);
                    array[i] = 1;
                    ctrl.widgetView = array;
                }
            }



            //for toggling on click
            // var temp = ctrl.widgetView.pop();
            // ctrl.widgetView.unshift(temp);
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