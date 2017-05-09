/**
 * Controller for the modal popup when creating
 * a new dashboard on the startup page
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('RenameDashboardController', RenameDashboardController);

    RenameDashboardController.$inject = ['$uibModalInstance', 'dashboardData', 'userService', 'dashboardId','dashboardName', '$scope'];
    function RenameDashboardController($uibModalInstance, dashboardData, userService, dashboardId, dashboardName, $scope) {

        var ctrl = this;

        // public variables
        ctrl.dashboardTitle = dashboardName;

        // public methods
        ctrl.submit = submit;

        ctrl.username = userService.getUsername();
        ctrl.authType = userService.getAuthType();

        dashboardData.allUsers(dashboardId).then(processUserResponse);

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
            dashboardData.promoteUserToOwner(dashboardId, user).then(
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
            dashboardData.demoteUserFromOwner(dashboardId, user).then(
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
                dashboardData
                    .rename(dashboardId, document.cdf.dashboardTitle.value)
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

    }
})();
