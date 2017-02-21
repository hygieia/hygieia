/** from http://stackoverflow.com/questions/24764802/angular-js-automatically-focus-input-and-show-typeahead-dropdown-ui-bootstra
 *
 * created by Yohai Rosen.
 * https://github.com/yohairosen
 * email: yohairoz@gmail.com
 * twitter: @distruptivehobo
 *
 * https://github.com/yohairosen/typeaheadFocus.git
 * Version: 0.0.1
 * License: MIT
 *
 * */
angular.module(HygieiaConfig.module + '.core')
    .directive('typeaheadFocus', function () {
        return {
            require: 'ngModel',
            link: function (scope, element, attr, ngModel) {

                // Array of keyCode values for arrow keys
                const ARROW_KEYS = [37,38,39,40];

                function manipulateViewValue(e) {
                    /* we have to check to see if the arrow keys were in the input because if they were trying to select
                     * a menu option in the typeahead, this may cause unexpected behavior if we were to execute the rest
                     * of this function
                     */
                    if( ARROW_KEYS.indexOf(e.keyCode) >= 0 )
                        return;

                    var viewValue = ngModel.$viewValue;

                    //restore to null value so that the typeahead can detect a change
                    if (ngModel.$viewValue == '') {
                        ngModel.$setViewValue(null);
                    }

                    //force trigger the popup
                    ngModel.$setViewValue('');

                    //set the actual value in case there was already a value in the input
                    ngModel.$setViewValue(viewValue || '');
                }

                /* trigger the popup on 'click' because 'focus'
                 * is also triggered after the item selection.
                 * also trigger when input is deleted via keyboard
                 */
                element.bind('click keyup', manipulateViewValue);

                //compare function that treats the empty space as a match

                scope.$emptyOrMatch = function (actual, expected) {
                    if (expected == ' ') {
                        return true;
                    }
                    return actual ? actual.toString().toLowerCase().indexOf(expected.toLowerCase()) > -1 : false;
                };
            }
        };
    });