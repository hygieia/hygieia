(function () {
    'use strict';

    var widget_state,
        config = {
            view: {
                defaults: {
                    title: 'Deploy' // widget title
                },
                controller: 'deployViewController',
                controllerAs: 'deployView',
                templateUrl: 'components/widgets/deploy/view.html'
            },
            config: {
                controller: 'deployConfigController',
                controllerAs: 'deployConfig',
                templateUrl: 'components/widgets/deploy/config.html'
            },
            getState: getState
        };

    angular
        .module(HygieiaConfig.module)
        .directive('validregex', function() {
          return {
            require: 'ngModel',
            link: function(scope, elm, attrs, ctrl) {
              ctrl.$validators.validregex = function(modelValue, viewValue) {
                if (ctrl.$isEmpty(modelValue)) {
                  // consider empty input to be valid
                  return true;
                }
        
                try {
                    new RegExp(viewValue.replace(/^"(.*)"$/, '$1'));
                } catch (e) {
                    // it is invalid
                    return false;
                }
                
                // it is valid
                return true;
              };
            }
          };
        })
        .config(register);

    register.$inject = ['widgetManagerProvider', 'WidgetState'];
    function register(widgetManagerProvider, WidgetState) {
        widget_state = WidgetState;
        widgetManagerProvider.register('deploy', config);
    }

    function getState(widgetConfig) {
        return HygieiaConfig.local || widgetConfig.id ?
            widget_state.READY :
            widget_state.CONFIGURE;
    }
})();
