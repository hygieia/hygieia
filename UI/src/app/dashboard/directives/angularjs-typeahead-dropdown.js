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
                "        <div class=input-group>" +
                "            <input class=form-control placeholder=\"{{config.placeholder || 'Select or type...'}}\" ng-model=mdl[config.optionLabel] typeahead=\"op[config.optionLabel] for op in options | filter:$viewValue\" typeahead-editable=false typeahead-on-select=\"onSelect($item, $model, $label)\" ng-required=\"required\" ng-disabled=\"disabled\"> " +
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
                scope: { mdl: "=ngModel", options: "=",  config: "=?", events: "=", required: "=?ngRequired", disabled: "=?ngDisabled" },
                require: "ngModel",
                replace: true,
                link: function($scope, $element, $attrs) {
                    $scope.externalEvents = {
                        onItemSelect: angular.noop
                    };
                    var defaults = {
                        modelLabel: 'id',
                        optionLabel: 'label',
                        btnClass: 'btn-default'
                    };
                    angular.extend(defaults, $scope.config);
                    $scope.config = defaults;
                    angular.extend($scope.externalEvents, $scope.events || []);
                },

                controller: ["$scope",
                    function (a) {
                        a.onSelect = function (i) {
                            a.mdl = i;
                            if (a.events !== undefined) {
                                a.events.onItemSelect(i);
                            }
                        }
                    }
                ]
            }
        });
