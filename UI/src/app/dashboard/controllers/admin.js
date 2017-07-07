/**
 * Controller for administrative functionality
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('AdminController', AdminController);


    AdminController.$inject = ['$scope', 'dashboardData', '$location','$uibModal', 'userService', 'authService', 'userData', 'dashboardService'];
    function AdminController($scope, dashboardData, $location, $uibModal, userService, authService, userData, dashboardService) {
        var ctrl = this;
        if (userService.isAuthenticated() && userService.isAdmin()) {
            $location.path('/admin');
        }
        else {
            console.log("Not authenticated redirecting");
            $location.path('#');
        }

        ctrl.storageAvailable = localStorageSupported;
        ctrl.showAuthentication = userService.isAuthenticated();
        ctrl.templateUrl = "app/dashboard/views/navheader.html";
        ctrl.username = userService.getUsername();
        ctrl.authType = userService.getAuthType();
        ctrl.login = login;
        ctrl.logout = logout;
        ctrl.editDashboard = editDashboard;
        ctrl.generateToken = generateToken;

        $scope.tab="dashboards";

        // list of available themes. Must be updated manually
        ctrl.themes = [
            {
                name: 'Dash',
                filename: 'dash'
            },
            {
                name: 'Dash for display',
                filename: 'dash-display'
            },
            {
                name: 'Bootstrap',
                filename: 'default'
            },
            {
                name: 'BS Slate',
                filename: 'slate'
            }];

        // used to only show themes option if local storage is available
        if(localStorageSupported) {
            ctrl.theme = localStorage.getItem('theme');
        }


        // ctrl.dashboards = []; don't default since it's used to determine loading

        // public methods
        ctrl.deleteDashboard = deleteDashboard;
        ctrl.applyTheme = applyTheme;


        // request dashboards
        dashboardData.search().then(processResponse);
        userData.getAllUsers().then(processUserResponse);
        userData.apitokens().then(processTokenResponse);


        //implementation of logout
        function logout() {
            authService.logout();
            $location.path("/login");
        }

        function login() {
          $location.path("/login")
        }

        // method implementations
        function applyTheme(filename) {
            if(localStorageSupported) {
                localStorage.setItem('theme', filename);
                location.reload();
            }
        }

        function deleteDashboard(id) {
            dashboardData.delete(id).then(function() {
                _.remove(ctrl.dashboards, {id: id});
            });
        }

        function editDashboard(item)
        {
            console.log("Edit Dashboard in Admin");

            var mymodalInstance=$uibModal.open({
                templateUrl: 'app/dashboard/views/editDashboard.html',
                controller: 'EditDashboardController',
                controllerAs: 'ctrl',
                resolve: {
                    dashboardItem: function() {
                        return item;
                    }
                }
            });

            mymodalInstance.result.then(function success() {
                dashboardData.search().then(processResponse);
                userData.getAllUsers().then(processUserResponse);
                userData.apitokens().then(processTokenResponse);
            });

        }

        function generateToken()
        {
            console.log("Generate token in Admin");

            var mymodalInstance=$uibModal.open({
                templateUrl: 'app/dashboard/views/generateApiToken.html',
                controller: 'GenerateApiTokenController',
                controllerAs: 'ctrl',
                resolve: {
                }
            });

            mymodalInstance.result.then(function(condition) {
                window.location.reload(false);
            });

        }

        function processResponse(data) {
            ctrl.dashboards = [];
            for (var x = 0; x < data.length; x++) {
                ctrl.dashboards.push({
                    id: data[x].id,
                    name: dashboardService.getDashboardTitle(data[x]),
                    type: data[x].type,
                    validServiceName:  data[x].validServiceName,
                    validAppName: data[x].validAppName,
                    configurationItemBusServName:  data[x].configurationItemBusServName,
                    configurationItemBusAppName:  data[x].configurationItemBusAppName,
                });
            }
        }

        function processUserResponse(response) {
            $scope.users = response.data;
        }

        function processTokenResponse(response) {
            $scope.apitokens = response.data;
        }

        $scope.navigateToTab = function(tab) {
          $scope.tab=tab;
        }

        $scope.isActiveUser = function(user) {
          if(user.authType === ctrl.authType && user.username === ctrl.username) {
            return true;
          }
          return false;
        }

        $scope.promoteUserToAdmin = function(user) {
          userData.promoteUserToAdmin(user).then(
            function(response) {
              var index = $scope.users.indexOf(user);
              $scope.users[index] = response.data;
            },
            function(error) {
              $scope.error = error;
            }
        );
        }

        $scope.demoteUserFromAdmin = function(user) {
          userData.demoteUserFromAdmin(user).then(
            function(response) {
              var index = $scope.users.indexOf(user);
              $scope.users[index] = response.data;
            },
            function(error) {
              $scope.error = error;
            }
        );
        }

    }
})();
