/**
 * Controller for the modal popup when creating
 * a new dashboard on the startup page
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('EditDashboardController', EditDashboardController);

    EditDashboardController.$inject = ['$uibModalInstance', 'dashboardData', 'cmdbData','userService', 'dashboardItem', '$scope', 'dashboardService'];
    function EditDashboardController($uibModalInstance, dashboardData, cmdbData, userService, dashboardItem, $scope, dashboardService) {

        var ctrl = this;
        // public variables


        ctrl.dashboardType = dashboardItem.type;
        ctrl.dashboardTitle = getDashboardTile(dashboardItem);
        ctrl.configurationItemBusServ = dashboardItem.configurationItemBusServName;
        ctrl.configurationItemBusApp = dashboardItem.configurationItemBusAppName;
        ctrl.tabs = [
            { name: "Dashboard Title"},
            { name: "Business Service/ Application"},
            { name: "Owner Information"}

        ];
        ctrl.tabView = ctrl.tabs[0].name;

        // public methods
        ctrl.submitTileName = submitTileName;
        ctrl.submitBusServOrApp = submitBusServOrApp;
        ctrl.getConfigItem = getConfigItem;
        ctrl.getDashboardTile = getDashboardTile;
        ctrl.getBusAppToolText = getBusAppToolText;
        ctrl.getBusSerToolText = getBusSerToolText;
        ctrl.tabToggleView = tabToggleView;
        ctrl.setConfigItemAppId = setConfigItemAppId;
        ctrl.setConfigItemComponentId = setConfigItemComponentId;
        ctrl.isValidBusServName = isValidBusServName;
        ctrl.isValidBusAppName = isValidBusAppName;

        ctrl.validBusServName = isValidBusServName();
        ctrl.validBusAppName = isValidBusAppName();
        ctrl.username = userService.getUsername();
        ctrl.authType = userService.getAuthType();

        dashboardData.allUsers(dashboardItem.id).then(processUserResponse);

        function processUserResponse(response) {
            $scope.users = response;
        }

        $scope.isActiveUser = function(user) {
            if(user.authType === ctrl.authType && user.username === ctrl.username) {
                return true;
            }
            return false;
        }

        $scope.promoteUserToAdmin = function(user) {
            dashboardData.promoteUserToOwner(dashboardItem.id, user).then(
                function(response) {
                    var index = $scope.users.indexOf(user);
                    $scope.users[index] = response.data;
                    var roles = $scope.users[index].authorities;
                },
                function(error) {
                    $scope.error = error;
                }
            );
        }

        $scope.demoteUserFromAdmin = function(user) {
            dashboardData.demoteUserFromOwner(dashboardItem.id, user).then(
                function(response) {
                    var index = $scope.users.indexOf(user);
                    $scope.users[index] = response.data;
                    var roles = $scope.users[index].authorities;
                },
                function(error) {
                    $scope.error = error;
                }
            );
        }

        function submitTileName(form) {

            form.dashboardTitle.$setValidity('renameError', true);

            if (form.$valid) {
                dashboardData
                    .rename(dashboardItem.id,
                        document.cdf.dashboardTitle.value
                    )
                    .success(function (data) {
                })
                    .error(function(data){

                    form.dashboardTitle.$setValidity('renameError', false);
                });
            }
            else
            {
                form.dashboardTitle.$setValidity('renameError', false);
            }

        }
        function submitBusServOrApp(form) {
            resetFormValidation(form);
            if (form.$valid) {

                var submitData = {
                        configurationItemBusServObjectId: dashboardService.getBusinessServiceId(ctrl.configurationItemBusServ),
                        configurationItemBusAppObjectId:  dashboardService.getBusinessApplicationId(ctrl.configurationItemBusApp)
                    };
                dashboardData
                    .updateBusItems(dashboardItem.id,submitData)
                    .success(function (data) {

                    })
                    .error(function (data) {
                        if(data){
                            ctrl.dupErroMessage = data;
                        }

                        form.configurationItemBusServ.$setValidity('dupBusServError', false);
                        form.configurationItemBusApp.$setValidity('dupBusAppError', false);
                    });
            }

        }



        function getConfigItem(type ,filter) {
            return cmdbData.getConfigItemList(type, {"search": filter, "size": 20}).then(function (response){
                return response;
            });
        }
        function getDashboardTile(item){
            var subName = dashboardItem.name.substring(0, dashboardItem.name.indexOf('-'));

            return subName ? subName : dashboardItem.name
        }

        function setConfigItemAppId(id){
            ctrl.validBusServName = true;
            dashboardService.setBusinessServiceId(id);
        }

        function setConfigItemComponentId(id){
            ctrl.validBusAppName = true;
            dashboardService.setBusinessApplicationId(id);
        }

        function getBusAppToolText(){
            return dashboardService.getBusAppToolTipText();
        }

        function getBusSerToolText(){
            return dashboardService.getBusSerToolTipText();
        }
        function tabToggleView(index) {
            ctrl.dupErroMessage = "";
            ctrl.tabView = typeof ctrl.tabs[index] === 'undefined' ? ctrl.tabs[0].name : ctrl.tabs[index].name;
        };
        function resetFormValidation(form){
            ctrl.dupErroMessage = "";
            form.configurationItemBusServ.$setValidity('dupBusServError', true);
            if(form.configurationItemBusApp){
                form.configurationItemBusApp.$setValidity('dupBusAppError', true);
            }

        }
        function isValidBusServName(){
            var valid = true;
            if(dashboardItem.configurationItemBusServName != undefined && !dashboardItem.validServiceName){
                valid = false;
            }
            return valid;
        }
        function isValidBusAppName(){
            var valid = true;
            if(dashboardItem.configurationItemBusAppName != undefined && !dashboardItem.validAppName){
                valid = false;
            }
            return valid;
        }
    }
})();
