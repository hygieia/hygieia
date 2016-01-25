/**
 * Modified version of the dropdown taken from
 * http://full360solutions.github.io/angularjs-typeahead-dropdown/
 *
 * Added ability to customize placeholder
 */

"use strict";
angular.module("typeaheadDropdown.tpl", [])
    .run(["$templateCache",
        function (a) {
            a.put("templates/typeaheadDropdown.tpl.html",
                "<div class='typeahead-dropdown'>" +
                "    <div ng-if=options class=dropdown dropdown>" +
                "        <div class=input-group><div ng-if='noResults'>No Results</div>" +
                "            <input class=\"form-control typeahead-dropdown-input\" placeholder=\"{{config.placeholder}}\" ng-model=mdl typeahead=\"op[config.optionLabel] for op in options | filter:$viewValue | orderBy:config.optionLabel\" typeahead-no-results=\"noResults\" typeahead-editable=false typeahead-on-select=\"onSelect($item, $model, $label)\" ng-required=\"required\" ng-disabled=\"disabled\" ng-blur=\"onBlur($viewValue)\"> " +
                "            <span class=input-group-btn>" +
                "                <button class=\"btn {{config.btnClass}} dropdown-toggle\" dropdown-toggle ng-disabled=\"disabled\">" +
                "                    <span class=caret></span>" +
                "                </button>" +
                "            </span>" +
                "        </div>" +
                "        <ul class=dropdown-menu role=menu style=max-height:200px;overflow-y:auto>" +
                "            <li ng-repeat=\"op in options\">" +
                "                <a href ng-click=onSelect(op)>{{op[config.optionLabel]}}</a>" +
                "            </li>" +
                "        </ul>" +
                "    </div>" +
                "</div>");
        }
    ]);

    angular.module("apg.typeaheadDropdown", ["typeaheadDropdown.tpl", "ui.bootstrap"])
        .directive("typeaheadDropdown", function() {
            return {
                templateUrl: "templates/typeaheadDropdown.tpl.html",
                scope: { mdl: "=ngModel", options: "=",  config: "=?", events: "=", required: "=?ngRequired", disabled: "=?ngDisabled"},
                require: "ngModel",
                replace: true,
                link: function($scope, $element, $attrs) {
                    $scope.externalEvents = {
                        onItemSelect: angular.noop
                    };
                    var defaults = {
                        optionLabel: 'label',
                        btnClass: 'btn-default',
                        placeholder: 'Select or type...'
                    };
                    angular.extend(defaults, $scope.config);
                    $scope.config = defaults;
                    angular.extend($scope.externalEvents, $scope.events || []);
                },

                controller: ["$scope",
                    function (a) {
                        a.noResults = false;

                        a.onSelect = function (i) {
                            a.mdl = i;
                            if (a.events !== undefined) {
                                a.events.onItemSelect(i);
                            }
                        };

                        a.onBlur = function (val) {
                            for(var x=0;x<a.options.length;x++) {

                            }
                        }
                    }
                ]
            }
        });
