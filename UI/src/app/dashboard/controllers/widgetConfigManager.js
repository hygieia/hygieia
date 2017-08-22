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

            $event.target.classList.toggle("added");
        }

        function addWidget(widgetTitle) {
            var newWidget = { x:0, y:0, width:4, height:1,order :ctrl.count++ };
            $scope.widgets[widgetTitle] = newWidget;
        };

        function removeWidget(title, $event) {
            if ($event != null) document.getElementById(title + '-button').classList.remove('added');
            delete $scope.widgets[title];
            ctrl.count--;
        };

        function saveDashboard($event,form) {
            var widgets = [];
            var order=[];
            _($scope.widgets).forEach(function(widget){
                var title = widget.title;

            });
            for (var title in $scope.widgets) {
                widgets.push(title);
                var obj = $scope.widgets[title];
                removeWidget(title, $event);
                order[obj.order] = title;
            }

            var submitData = {
                template: ctrl.createDashboardData.template,
                title: ctrl.createDashboardData.title,
                type: ctrl.createDashboardData.type,
                applicationName: ctrl.createDashboardData.applicationName,
                componentName: ctrl.createDashboardData.componentName,
                configurationItemBusServObjectId: ctrl.createDashboardData.configurationItemBusServObjectId,
                configurationItemBusAppObjectId: ctrl.createDashboardData.configurationItemBusAppObjectId,
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