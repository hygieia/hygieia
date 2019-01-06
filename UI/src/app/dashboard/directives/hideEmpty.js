/**
 * Standard delete directive for various components
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .directive('hideEmpty', function () {
            return {
                restrict: 'A',

                // link: function(scope,element, attrs, ctrl) {
                //     //if true (if widget is empty), hide the widget
                //     if(scope.$eval(attrs.hideEmpty)) {
                //         // element.hide();
                //         element.replaceWith('<div class="row"><div class="col-md-12"><div class="widget-body"><br>No data found.</div></div></div>');
                //         // scope.dataMessage = "No data found.";
                //     }
                //     else {
                //         element.show();
                //     }
                // }

                link: function(scope,element, attrs, ctrl) {
                    //if true (if widget is empty), hide the widget
                    if(scope.$eval(attrs.hideEmpty)) {
                        // element.hide();
                        element.replaceWith('<div class="row"><div class="col-md-12"><div class="widget-body"><br>No data found.</div></div></div>');
                        // scope.dataMessage = "No data found.";
                    }
                    else {
                        element.show();
                    }
                }
            };
        });
})();