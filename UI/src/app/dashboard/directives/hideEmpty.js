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

                link: function(scope, element, attrs) {
                    attrs.$observe('hideEmpty', function(){
                       // Check if the inputs are all null, meaning the widget is empty (0 is NOT empty, could be a value)
                        var dataList = scope.$eval(attrs.hideEmpty);
                        var hideWidget = dataList.every(x => (!x && x != '0'));

                        //if true (if widget is empty), hide the widget
                        if(hideWidget) {
                            // hide the widget data within the section (not the current element)
                            element.children().first().hide();

                            // if "no data found" message already added, make sure it is shown. Otherwise, append it to the HTML
                            if (document.getElementById('noDataMsg')){
                                angular.element(document.getElementById('noDataMsg')).show();
                            } else {
                                element.append('<div id="noDataMsg" class="row"><div class="col-md-12"><div class="widget-body"><br>No data found.</div></div></div>');
                            }
                        }
                        else {
                            // display the widget and hide "No data found" message
                            element.children().first().show();
                            angular.element(document.getElementById('noDataMsg')).hide();
                        }
                    }, true);
                }
            };
        });
})();