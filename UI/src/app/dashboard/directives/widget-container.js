/**
 * Manages all communication with widgets and placeholders
 * Should be included at the root of the layout file and pass in the dashboard
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .directive('widgetContainer', widgetContainer);

    widgetContainer.$inject = ['$compile'];
    function widgetContainer($compile) {
        return {
            restrict: 'A',
            scope: {
                dashboard: '='
            },
            link: link,
            controller: controller

        };

        function controller($scope) {
            /*jshint validthis:true */
            if (!$scope.dashboard) {
                throw new Error('dashboard not accessible by widget-container directive');
            }

            // keep track of the various types of widgets
            $scope.placeholders = [];
            $scope.registeredWidgets = {};
            $scope.processedWidgetNames = [];

            // public methods
            this.registerPlaceholder = registerPlaceholder;
            this.registerWidget = registerWidget;
            this.upsertWidget = upsertWidget;
            this.upsertComponent = upsertComponent;

            // add a new placeholder
            function registerPlaceholder(placeholder) {
                $scope.placeholders.push(placeholder);
            }

            // add a new widget
            function registerWidget(widget) {
                if(!widget.attrs.name) {
                    throw new Error('Widget name not defined');
                }

                var name = widget.attrs.name = widget.attrs.name.toLowerCase();

                if(!$scope.registeredWidgets[name]) {
                    $scope.registeredWidgets[name] = [];
                }

                $scope.registeredWidgets[name].push(widget);

                // give the widget an id based on index
                /**
                 * TODO: this widget naming is a hack that won't work with placeholders
                 * and configuring widgets out of order in a layout.
                 * Maybe adding a placeholder index to the widget
                 */
                var widgetId = name + ($scope.registeredWidgets[name].length - 1);
                var foundConfig = {options: {id: widgetId}};
                var configInDashboard = false;

                // get currently saved widget config
                _($scope.dashboard.widgets).forEach(function (config) {
                    if (config.options && config.options.id == widgetId) {
                        // process widget with the config object
                        foundConfig = config;
                        configInDashboard = true;
                    }
                });

                if (widget.callback) {
                    $scope.processedWidgetNames.push(widgetId);
                    widget.callback(configInDashboard, foundConfig, $scope.dashboard);
                }
            }

            function upsertComponent(newComponent) {
                // not all widgets have to have components so this may be null
                if(newComponent == null) {
                    return;
                }

                // Currently there will only be one component on the dashboard, but this logic should work
                // when that changes and multiple are available
                var foundComponent = false;
                _($scope.dashboard.application.components).forEach(function (component, idx) {
                    if(component.id == newComponent.id) {
                        foundComponent = true;
                        $scope.dashboard.application.components[idx] = newComponent;
                    }
                });

                if(!foundComponent) {
                    $scope.dashboard.application.components.push(newComponent);
                }
            }

            function upsertWidget(newConfig) {
                // update the local config id
                // widget directive handles api updates
                var foundMatch = false;
                _($scope.dashboard.widgets)
                    .filter(function(config) {
                        return config.options.id === newConfig.options.id;
                    }).forEach(function (config, idx) {
                        foundMatch = true;

                        $scope.dashboard.widgets[idx] = angular.extend(config, newConfig);
                    });

                if(!foundMatch) {
                    $scope.dashboard.widgets.push(newConfig);
                }
            }
        }

        // TODO: loop through placeholders and place any widgets not already processed in them
        function link($scope) {
            // process placeholders
            // get the dashboard controller (just need widgets?)
            if ($scope.placeholders.length === 0) {
                return;
            }

            _($scope.dashboard.widgets)
                .filter(function (widget) {
                    return $scope.processedWidgetNames.indexOf(widget.options.id) == -1;
                })
                .forEach(function (item, idx) {
                    var remainder = idx % $scope.placeholders.length;
                    var widget = $scope.dashboard.widgets[idx];

                    var el = $compile('<widget name="' + widget.name + '"></widget>')($scope);

                    $scope.placeholders[remainder].element.append(el);
                });
        }
    }
})();