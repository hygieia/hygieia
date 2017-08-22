/**
 * Controller for the template CRUD page
 */
(function() {
    'use strict';

    angular.module(HygieiaConfig.module)
        .controller('EditTemplateController', EditTemplateController);

    EditTemplateController.$inject = [ '$scope', '$location', 'userService', 'widgetManager','templateMangerData','$uibModalInstance','templateObject','dashboardData'];
    function EditTemplateController($scope, $location, userService, widgetManager,templateMangerData,$uibModalInstance,templateObject,dashboardData) {
        var ctrl = this;

        // public variables
        ctrl.search = '';
        ctrl.myadmin = '';
        ctrl.username = userService.getUsername();
        ctrl.showAuthentication = userService.isAuthenticated();
        ctrl.templateName ='';
        ctrl.count = 0;
        ctrl.templateDetails ={};
        ctrl.templateObj = templateObject;

        // public methods
        ctrl.saveTemplate = saveTemplate;

        if(templateObject!=null){
            ctrl.edit = true;
            ctrl.existingWidgets = templateObject.widgets;
            ctrl.existingOrder = templateObject.order;
            ctrl.templateName = templateObject.template;
            ctrl.templateId = templateObject.id;
            // get all widgets
            ctrl.widgets = widgetManager.getWidgets();
            // collection to hold selected widgets
            ctrl.widgetSelections={};
            // iterate through widgets and add existing widgets for dashboard
            _.map(ctrl.widgets, function (value, key) {
                var k = key;
                if(key!='')
                if(ctrl.existingWidgets.indexOf(k)>-1){
                    ctrl.widgetSelections[k] = true;
                }else{
                    ctrl.widgetSelections[k] = false;
                }

            });
            _(ctrl.widgets).forEach(function (widget) {
                var wd = widget;
                ctrl.widgetSelections[widget.title]= false;
            });
        }

        if (ctrl.username === 'admin') {
            ctrl.myadmin = true;
        }

        $scope.options = {
            cellHeight: 200,
            verticalMargin: 10
        };

        // Save template - after edit
        function saveTemplate($event,form) {
            ctrl.adjustedOrder = [];
            findSelectedWidgets();
            findOrder();
            ctrl.adjustedOrder = cleanArray(ctrl.order);
            var submitData = {
                template: ctrl.templateName,
                widgets: ctrl.selectedWidgets,
                order:ctrl.adjustedOrder
            };

            var dashboardsList = [];
            dashboardData.search().then(function (response) {
                _(response).forEach(function(dashboard){
                    if(dashboard.template ==ctrl.templateName){
                        dashboardsList.push(dashboard.title);
                    }
                });
                if(dashboardsList.length>0){
                    var dash ='';
                    for(var dashboardTitle in dashboardsList){
                         dash = dash+'\n'+dashboardsList[dashboardTitle];
                     }
                    swal({
                        title: 'Template used in existing dashboards',
                        text: dash,
                        html: true,
                        type: "warning",
                        showCancelButton: true,
                        showConfirmButton:true,
                        closeOnConfirm: true,
                        closeOnCancel: true},
                        function(){
                            if(form.$valid ){
                                templateMangerData.updateTemplate(ctrl.templateId,submitData) .then(function (data) {

                                    // redirect to the new dashboard
                                    var result = data;
                                    var res = result;
                                    ctrl.templateName ="";
                                    var obj = false;
                                    obj = {
                                        tabName: 'templates'
                                    };
                                    $uibModalInstance.close(obj);

                                }, function(response) {
                                    var msg = 'An error occurred while editing the Template';
                                    swal(msg);
                                });
                            }
                    });
                }else{
                    if(form.$valid ){
                        templateMangerData.updateTemplate(ctrl.templateId,submitData) .then(function (data) {
                            var result = data;
                            var res = result;
                            ctrl.templateName ="";
                            var obj = false;
                            obj = {
                                tabName: 'templates'
                            };
                            $uibModalInstance.close(obj);
                        }, function(response) {
                            var msg = 'An error occurred while editing the Template';
                            swal(msg);
                        });
                    }
                }
            });
        }

        // adjust array after edit - includes additions and deletions of widgets in existing collection of widgets
        function cleanArray(actual) {
            var newArray = new Array();
            for (var i = 0; i < actual.length; i++) {
                if (actual[i]) {
                    newArray.push(actual[i]);
                }
            }
            return newArray;
        }

        // find selected widgets and add it to collection
        function findSelectedWidgets(){
            ctrl.selectedWidgets = [];
            for(var selectedWidget in ctrl.widgetSelections){
                var s = ctrl.widgetSelections[selectedWidget];
                if(s){
                    ctrl.selectedWidgets.push(selectedWidget);
                }
            }
        }

        //find the existing order of widget layout
        function findOrder(){
            ctrl.order=[];
            var counter = ctrl.existingOrder.length;
                _(ctrl.selectedWidgets).forEach(function (selectedWidget) {
                   var index =  ctrl.existingOrder.indexOf(selectedWidget);
                    if(index>-1){
                        ctrl.order[index] = selectedWidget;
                    }
             });
            var orderLength = ctrl.order.length;
            _(ctrl.selectedWidgets).forEach(function (selectedWidget) {
                var index =  ctrl.existingOrder.indexOf(selectedWidget);
                if(index==-1){
                    ctrl.order[orderLength++] = selectedWidget;
                }
            });
        }

        function admin() {
            console.log('sending to admin page');
            $location.path('/admin');
        }

    }
})();