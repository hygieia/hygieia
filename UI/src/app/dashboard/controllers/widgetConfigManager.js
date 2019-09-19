/**
 * Controller for the template CRUD page
 */
(function() {
    'use strict';

    angular.module(HygieiaConfig.module)
        .controller('WidgetConfigManager', TemplateController);

    TemplateController.$inject = [ '$scope', '$location', 'userService', 'widgetManager', 'DashboardType','$uibModalInstance','createDashboardData','dashboardData'];
    function TemplateController($scope, $location, userService, widgetManager,DashboardType,$uibModalInstance,createDashboardData,dashboardData) {
        var ctrl = this;

        // public variables
        ctrl.search = '';
        ctrl.myadmin = '';
        ctrl.username = userService.getUsername();
        ctrl.showAuthentication = userService.isAuthenticated();
        ctrl.templateUrl = 'app/dashboard/views/navheader.html';
        ctrl.dashboardTypeEnum = DashboardType;

        // public methods
        ctrl.admin = admin;
        ctrl.toggleWidget = toggleWidget;
        ctrl.removeWidget = removeWidget;
        ctrl.addWidget = addWidget;
        ctrl.onChange = onChange;
        ctrl.onDragStart = onDragStart;
        ctrl.onResizeStart = onResizeStart;
        ctrl.onResizeStop = onResizeStop;
        ctrl.saveDashboard = saveDashboard;

        ctrl.templateName ='';
        ctrl.count = 0;
        ctrl.templateDetails ={};

        ctrl.createDashboardData = createDashboardData;
        if (ctrl.username === 'admin') {
            ctrl.myadmin = true;
        }

        $scope.widgets = {};
        $scope.widgetCountByType={};
        ctrl.widgets = widgetManager.getWidgets();

        $scope.options = {
            cellHeight: 200,
            verticalMargin: 10
        };

        function toggleWidget(widget, $event) {
            if (widget in $scope.widgets) {
                removeWidget(widget);
            }else{
                addWidget(widget);
            }
        }

        function addWidget(widgetType, $event) {
            var newWidget = { x:0, y:0, width:4, height:1,order :ctrl.count++, type: widgetType };
            var title;
            if (widgetType === 'cloud' || widgetType === 'pipeline') {
                // restrict these to 1!
                title = widgetType;
                $scope.widgetCountByType[widgetType]=1;
            } else if ($scope.widgetCountByType[widgetType]) {
                var count =$scope.widgetCountByType[widgetType]+1;
                $scope.widgetCountByType[widgetType]=count;
                title=widgetType+count;
            } else {
                $scope.widgetCountByType[widgetType]=1;
                var count=1;
                title=widgetType+count;
            }
            $scope.widgets[title] = newWidget;
        };

        function removeWidget(title, $event) {
            delete $scope.widgets[title];
        };

        function saveDashboard($event,form) {
            var widgets = [];
            var order=[];
            _($scope.widgets).forEach(function(widget, title){
                var activeWidget = {title:title, type: widget.type};
                widgets.push(activeWidget);
                removeWidget(activeWidget.title, $event);
                order[widget.order] = activeWidget.title;
            });

            var submitData = {
                template: ctrl.createDashboardData.template,
                title: ctrl.createDashboardData.title,
                type: ctrl.createDashboardData.type,
                applicationName: ctrl.createDashboardData.applicationName,
                componentName: ctrl.createDashboardData.componentName,
                configurationItemBusServName: ctrl.createDashboardData.configurationItemBusServName,
                configurationItemBusAppName: ctrl.createDashboardData.configurationItemBusAppName,
                scoreEnabled : ctrl.createDashboardData.scoreEnabled,
                scoreDisplay : ctrl.createDashboardData.scoreDisplay,
                activeWidgets: widgets
            };


            if(form.$valid ){
                dashboardData
                    .create(submitData)
                    .success(function (data) {
                        // redirect to the new dashboard
                        $location.path('/dashboard/' + data.id);
                        // close dialog
                        $uibModalInstance.dismiss();
                    })
                    .error(function (data) {
                        if (data.errorCode === 401) {
                            $modalInstance.close();
                        } else if (data.errorCode === -13) {

                            if (data.errorMessage) {
                                ctrl.dupErroMessage = data.errorMessage;
                            }

                            form.configurationItemBusServ.$setValidity('dupBusServError', false);
                            form.configurationItemBusApp.$setValidity('dupBusAppError', false);

                        } else {
                            form.dashboardTitle.$setValidity('createError', false);
                        }

                    });
            }

        }

        function onChange(event, items) {
            console.log("onChange event: "+event+" items:"+items);
        };

        function onDragStart(event, ui) {
            console.log("onDragStart event: "+event+" ui:"+ui);
        };

        function onDragStop(event, ui) {
            console.log("onDragStop event: "+event+" ui:"+ui);
        };

        function onResizeStart(event, ui) {
            console.log("onResizeStart event: "+event+" ui:"+ui);
        };

        function onResizeStop(event, ui) {
            console.log("onResizeStop event: "+event+" ui:"+ui);
        };

        function admin() {
            console.log('sending to admin page');
            $location.path('/admin');
        }

    }
})();
