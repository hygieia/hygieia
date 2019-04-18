/**
 * Directive to determine whether or not a widget has data based on shown values
 *
 * Checks each value and if all of them are null, then the widget is empty. If ANY of the inputs have a value then the widget should be displayed.
 * hideEmpty should be added as a wrapper around the widget so that the inner div (with the data) can be hidden or shown while still displaying the overall div so that the error message can be shown.
 *
 * Additions added so that if there are multiple widgets within section, the error message will only be displayed once- if it is NOT already displayed
 *
 * TODO: Add additional functionality to determine if there is already data within the widget so "No data found" is only displayed on a completely empty widget **Should only be an issue for widgets where there are two groups (ex: Static Analysis and Tests)
 * TODO: Account for empty arrays (currently checking for first value)
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .directive('hideEmpty', function () {
            return {
                restrict: 'A',
                link: function (scope, element, attrs) {
                    attrs.$observe('hideEmpty', function () {
                        // Check if the inputs are all null, meaning the widget is empty (0 is NOT empty, could be a value)
                        var dataList = scope.$eval(attrs.hideEmpty);
                        var hideWidget;

                        if (dataList){
                            hideWidget = dataList.every(x => (!x && x != '0'));
                        } else {
                            hideWidget = true;
                        }

                        // Find "No data found" message if it exists (within the full widget)
                        var noDataFound = element.siblings('#noDataMsg');

                        //if true (if widget is empty), hide the widget
                        if (hideWidget) {
                            // hide the widget data within the section (the current element)
                            element.hide();

                            // if "no data found" message already added, make sure it is shown. Otherwise, append it to the HTML as a sibling
                            if (noDataFound.length > 0) {
                                noDataFound.show();
                            } else {
                                element.parent().append('<div id="noDataMsg" class="row"><div class="col-md-12">No data found.</div></div>');
                            }
                        } else {
                            // display the widget and hide "No data found" message
                            element.show();
                            noDataFound.hide();
                        }
                    }, true);
                }
            };
        });
})();