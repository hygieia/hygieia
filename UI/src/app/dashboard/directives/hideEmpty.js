/**
 * Directive to determine whether or not a widget has data based on shown values
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .directive('hideEmpty', function () {
            return {
                restrict: 'A',
                link: function(scope, element, attrs) {
                    attrs.$observe('hideEmpty', function(){
                       // Check if the inputs are all null, meaning the widget is empty (0 is NOT empty, could be a value)
                        var dataList = scope.$eval(attrs.hideEmpty);
                        var hideWidget = dataList.every(x => (!x && x != '0'));

                        // Find "No data found" message if it exists (within the full widget)
                        var noDataFound = element.parent().find('#noDataMsg');

                        //if true (if widget is empty), hide the widget
                        if(hideWidget) {
                            // hide the widget data within the section (not the current element)
                            element.children().first().hide();

                            // if "no data found" message already added, make sure it is shown. Otherwise, append it to the HTML
                            if (noDataFound.length > 0){
                                 noDataFound.show();
                            }
                            else {
                                element.append('<div id="noDataMsg" class="row"><div class="col-md-12"><div class="widget-body"><br>No data found.</div></div></div>');
                            }
                        }
                        else {
                            // display the widget and hide "No data found" message
                            element.children().first().show();
                            noDataFound.hide();
                        }
                    }, true);
                }
            };
        });
})();