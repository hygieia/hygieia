/**
 * Controller for the modal popup when creating
 * a new dashboard on the startup page
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('RenameDashboardController', RenameDashboardController);

    RenameDashboardController.$inject = ['$uibModalInstance', 'dashboardData', 'cmdbData','userService', 'dashboardItem', '$scope'];
    function RenameDashboardController($uibModalInstance, dashboardData,cmdbData, userService,dashboardItem,$scope) {

        var ctrl = this;

        // public variables
        ctrl.dashboardTitle =getDashboardTile(dashboardItem);
        ctrl.configurationItemApp = dashboardItem.configurationItemAppName;
        ctrl.configurationItemComponent = dashboardItem.configurationItemCompName;

        // public methods
        ctrl.submit = submit;
        ctrl.getConfigItem = getConfigItem;
        ctrl.getDashboardTile = getDashboardTile;
        ctrl.appendTitle = appendTitle;
        ctrl.submitAppChange = submitAppChange;
        ctrl.submitCompChange = submitCompChange;
        ctrl.setConfigItemAppId = cmdbData.setConfigItemAppId;
        ctrl.setConfigItemComponentId = cmdbData.setConfigItemComponentId;

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

        function submit(form) {

            form.dashboardTitle.$setValidity('renameError', true);

            if (form.$valid) {
                var title = ctrl.appendTitle(document.cdf.dashboardTitle.value, document.cdf.configurationItemApp.value,document.cdf.configurationItemComponent.value)
                dashboardData
                    .rename(dashboardItem.id,
                        title
                    )
                    .success(function (data) {
                        $uibModalInstance.close();
                        window.location.reload(false);
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
        function getConfigItem(type ,filter) {
            return cmdbData.getConfigItemList(type, {"search": filter, "size": 20}).then(function (response){
                return response;
            });
        }
        function getDashboardTile(item){
            var subName = dashboardItem.name.substring(0, dashboardItem.name.indexOf('-'));

            return subName ? subName : dashboardItem.name
        }
        function appendTitle(titleName, appName, compName){

            var configurationItemAppName = appName ?  "-" +appName : "";
            var configurationItemCompName = compName ?  "-" +compName : "";
            var title = titleName + configurationItemAppName + configurationItemCompName;

            return title ;
        }
        function submitAppChange(id, event){


                if (event.which != 13) {
                    dashboardData.updateAppId(dashboardItem.id, id).then(
                        function (response) {
                            //do something
                        },
                        function (error) {
                           // $scope.cdf.configurationItemApp.$setValidity('invalidName', false);
                        }
                    );
                }

        }
        function submitCompChange(id, event){

            if(event.which != 13){
                dashboardData.updateAppId(dashboardItem.id, id).then(
                    function(response) {
                        //do something
                    },
                    function(error) {
                        $scope.error = error;
                    }
                );
            }

        }
    }
})();
