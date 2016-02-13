/**
 * A modification of a formGroup plugin from https://gist.github.com/lpsBetty/3259e966947809465cbe
 *
 * This element directive is the suggested way to add form fields to your controls and config screens.
 * For the directive to work the name of the input property must match the name of the input element as
 * well as the field the input is bound to on the controller
 *
 * example:
 * <form-group input="myField" errors="{required:'My custom error', minlength: 'Need some more characters'}">
 *     <input type="text" name="myField" ng-model="ctrl.myField" required />
 * </form-group>
 *
 * instead of:
 * <div class="form-group" ng-class="{'has-error': form.myField.$invalid && form.$submitted}">
 *     <input type="text" name="myField" ng-model="ctrl.myField" required />
 *
 *     <p class="help-block" ng-if="form.myField.$error.required">My custom error</p>
 *     <p class="help-block" ng-if="form.myField.$error.minlength">Need some more characters</p>
 * </div>
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .directive('formGroup', function () {
            return {
                restrict: 'E',
                require: '^form',
                transclude: true,
                replace: true,
                scope: {
                    input: '@',
                    errors: '='
                },
                template: '<div class="form-group" ng-class="{\'has-error\':hasError}">' +
                '<div ng-transclude></div>' +
                '<div ng-if="hasError">' +
                '<p ng-repeat="(key,error) in form[input].$error" class="help-block" ng-if="error">{{messages[key] || key + " validation failed"}}</p>' +
                '</div></div>',
                link: function (scope, element, attrs, ctrl) {
                    scope.form = ctrl;
                    scope.formSubmitted = false;

                    // set up some custom messages
                    scope.messages = {
                        required: 'Please enter a value'
                    };
                    if(scope.errors) {
                        for(var x in scope.errors) {
                            scope.messages[x] = scope.errors[x];
                        }
                    }

                    scope.$parent.$watch(ctrl.$name + '.$submitted', function(submitted) {
                        scope.formSubmitted = submitted;
                        scope.hasError = scope.formSubmitted && !scope.fieldValid;
                    });

                    scope.$parent.$watch(ctrl.$name + '.' + scope.input + '.$valid', function(isValid) {
                        scope.fieldValid = isValid;
                        scope.hasError = scope.formSubmitted && !scope.fieldValid;
                    });
                }
            };
        });
})();