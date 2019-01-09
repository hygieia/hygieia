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
                scope: {
                    hideEmpty: "="
                },

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
                    attrs.$observe(attrs.hideEmpty, function(){
                        // Assume no data
                        var isEmpty = true;
                        var dataList = scope.$eval(attrs.hideEmpty);
                        scope.data = dataList;

                        // Check if any of the values have data (not empty)
                        for (var i=0; i < dataList.length; i++) {
                            // if data exists, widget is not empty
                            if (dataList[i] || dataList[i] == '0') {
                                isEmpty = false;
                            }
                        }
                        //if true (if widget is empty), hide the widget
                        if(isEmpty) {
                            // element.hide();
                            scope.result = "hide";
                            // element.replaceWith('<div class="row"><div class="col-md-12"><div class="widget-body"><br>No data found.</div></div></div>');
                            // scope.dataMessage = "No data found.";
                        }
                        else {
                            scope.result = "show";
                            element.show();
                        }
                    };
                }
            };
        });
})();