/**
 * Controller for administrative functionality
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('AdminController', AdminController);


    AdminController.$inject = ['$scope', 'dashboardData', '$location', '$uibModal', 'userService', 'authService', 'userData', 'dashboardService', 'templateMangerData', 'paginationWrapperService','$sce','$q','serviceAccountData','featureFlagsData'];
    function AdminController($scope, dashboardData, $location, $uibModal, userService, authService, userData, dashboardService, templateMangerData, paginationWrapperService,$sce,$q,serviceAccountData,featureFlagsData) {
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
        ctrl.goToManager = goToManager;
        ctrl.deleteTemplate = deleteTemplate;
        ctrl.viewTemplateDetails = viewTemplateDetails;
        ctrl.editTemplate = editTemplate;
        ctrl.deleteToken = deleteToken;
        ctrl.editToken = editToken;

        ctrl.pageChangeHandler = pageChangeHandler;
        ctrl.currentPage = currentPage;
        ctrl.pageSize = pageSize;
        ctrl.getPageSize = getPageSize;
        ctrl.search ='';
        ctrl.filterByTitle = filterByTitle;
        ctrl.getTotalItems = getTotalItems;
        ctrl.addServiceAccount = addServiceAccount;
        ctrl.editAccount = editAccount;
        ctrl.deleteAccount = deleteAccount;
        ctrl.addFeatureFlag = addFeatureFlag;
        ctrl.deleteFlags = deleteFlags;



        $scope.tab = "dashboards";

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
        if (localStorageSupported) {
            ctrl.theme = localStorage.getItem('theme');
        }

        // ctrl.dashboards = []; don't default since it's used to determine loading

        // public methods
        ctrl.deleteDashboard = deleteDashboard;
        ctrl.applyTheme = applyTheme;


        (function() {

            dashboardData.getPageSize().then(function (data) {
                pullDashboards();
            });
        })();


        function pullDashboards(type) {
            // request dashboards
            dashboardData.searchByPage({"search": '', "type": type, "size": getPageSize(), "page": 0})
                .then(processDashboardResponse, processDashboardError);


            paginationWrapperService.calculateTotalItems(type)
                .then (function () {
                    ctrl.totalItems = paginationWrapperService.getTotalItems();
                })

        }

        function processDashboardResponse(data) {
            ctrl.dashboards = paginationWrapperService.processDashboardResponse(data, ctrl.dashboardType);
        }

        function processDashboardError(data) {
            ctrl.dashboards = paginationWrapperService.processDashboardError(data);
        }

        function getPageSize() {
            return paginationWrapperService.getPageSize();
        }




        // request dashboards
        userData.getAllUsers().then(processUserResponse);
        userData.apitokens().then(processTokenResponse);
        templateMangerData.getAllTemplates().then(processTemplateResponse);
        serviceAccountData.getAllServiceAccounts().then(processServAccResponse);
        featureFlagsData.getFeatureFlagsData().then(processFeatureFlagResponse);


        function pageChangeHandler(pageNumber) {
            paginationWrapperService.pageChangeHandler(pageNumber)
                .then(function() {
                    ctrl.dashboards = paginationWrapperService.getDashboards();
                });
        }

        function getTotalItems() {
            return paginationWrapperService.getTotalItems();
        }


        function currentPage() {
            return paginationWrapperService.getCurrentPage();
        }

        function pageSize() {
            return paginationWrapperService.getPageSize();
        }

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
            if (localStorageSupported) {
                localStorage.setItem('theme', filename);
                location.reload();
            }
        }

        function deleteDashboard(id) {
            dashboardData.delete(id).then(function () {
                _.remove(ctrl.dashboards, {id: id});
            });
            paginationWrapperService.calculateTotalItems();
        }

        function editDashboard(item) {
            console.log("Edit Dashboard in Admin");

            var mymodalInstance = $uibModal.open({
                templateUrl: 'app/dashboard/views/editDashboard.html',
                controller: 'EditDashboardController',
                controllerAs: 'ctrl',
                resolve: {
                    dashboardItem: function () {
                        return item;
                    }
                }
            });

            mymodalInstance.result.then(function success() {
                dashboardData.search().then(processResponse);
                userData.getAllUsers().then(processUserResponse);
                userData.apitokens().then(processTokenResponse);
                templateMangerData.getAllTemplates().then(processTemplateResponse);
            });
        }

        function editToken(item) {
            console.log("Edit token in Admin");

            var mymodalInstance=$uibModal.open({
                templateUrl: 'app/dashboard/views/editApiToken.html',
                controller: 'EditApiTokenController',
                controllerAs: 'ctrl',
                resolve: {
                    tokenItem: function() {
                        return item;
                    }
                }
            });

            mymodalInstance.result.then(function() {
                userData.apitokens().then(processTokenResponse);
            });
        }

        function deleteToken(id) {
            userData.deleteToken(id).then(function() {
                _.remove( $scope.apitokens , {id: id});
            });
        }

        function generateToken() {
            console.log("Generate token in Admin");

            var mymodalInstance = $uibModal.open({
                templateUrl: 'app/dashboard/views/generateApiToken.html',
                controller: 'GenerateApiTokenController',
                controllerAs: 'ctrl',
                resolve: {}
            });

            mymodalInstance.result.then(function (condition) {
                window.location.reload(false);
            });
        }

        function editAccount(item) {
            console.log("Edit service account in Admin");

            var mymodalInstance=$uibModal.open({
                templateUrl: 'app/dashboard/views/edit-service-account.html',
                controller: 'EditServiceAccountController',
                controllerAs: 'ctrl',
                resolve: {
                    account: function() {
                        return item;
                    }
                }
            });

            mymodalInstance.result.then(function() {
                serviceAccountData.getAllServiceAccounts().then(processServAccResponse);
            });
        }

        function addServiceAccount() {
            console.log("Add service account");

            var mymodalInstance = $uibModal.open({
                templateUrl: 'app/dashboard/views/add-service-account.html',
                controller: 'AddServiceAccountController',
                controllerAs: 'ctrl',
                resolve: {}
            });

            mymodalInstance.result.then(function (condition) {
                window.location.reload(false);
            });
        }

        function addFeatureFlag() {
            console.log("Add Feature flags");

            var mymodalInstance = $uibModal.open({
                templateUrl: 'app/dashboard/views/add-featureflag.html',
                controller: 'AddFeatureFlagController',
                controllerAs: 'ctrl',
                resolve: {}
            });

            mymodalInstance.result.then(function (condition) {
                window.location.reload(false);
            });
        }

        function deleteFlags(id) {
            featureFlagsData.deleteFeatureFlags(id).then(function() {
                _.remove( ctrl.featureFlags , {id: id});
            });
        }


        function deleteAccount(id) {
            serviceAccountData.deleteAccount(id).then(function() {
                _.remove( ctrl.serviceAccounts , {id: id});
            });
        }


        function processResponse(data) {
            ctrl.dashboards = paginationWrapperService.processDashboardResponse({"data" : data});
        }

        function processUserResponse(response) {
            $scope.users = response.data;
        }

        function processTokenResponse(response) {
            $scope.apitokens = response.data;
        }

        function processTemplateResponse(data) {
            ctrl.templates = data;
        }

        function processServAccResponse(response){
            ctrl.serviceAccounts = response.data;
        }

        function processFeatureFlagResponse(response){
            ctrl.featureFlags = response.data;
        }

        // navigate to create template modal
        function goToManager() {
            var modalInstance = $uibModal.open({
                templateUrl: 'app/dashboard/views/templateManager.html',
                controller: 'TemplateController',
                controllerAs: 'ctrl',
                size: 'lg',
                resolve: {}
            }).result.then(function (config) {
                window.location.reload(true);
            });
        }

        // Edit template
        function editTemplate(item) {
            console.log("Edit Template in Admin");
            var mymodalInstance = $uibModal.open({
                templateUrl: 'app/dashboard/views/editTemplate.html',
                controller: 'EditTemplateController',
                controllerAs: 'ctrl',
                size: 'md',
                resolve: {
                    templateObject: function () {
                        return item;
                    }
                }
            });

            mymodalInstance.result.then(function success() {
                dashboardData.search().then(processResponse);
                userData.getAllUsers().then(processUserResponse);
                userData.apitokens().then(processTokenResponse);
                templateMangerData.getAllTemplates().then(processTemplateResponse);
            });
        }

        //Delete template
        function deleteTemplate(item) {
            var id = item.id;
            var dashboardsList = [];
            dashboardData.search().then(function (response) {
                _(response).forEach(function (dashboard) {
                    if (dashboard.template == item.template) {
                        dashboardsList.push(dashboard.title);
                    }
                });
                if (dashboardsList.length > 0) {
                    var dash = '';
                    for (var dashboardTitle in dashboardsList) {
                        dash = dash + '\n' + dashboardsList[dashboardTitle];
                    }
                    swal({
                        title: 'Template used in existing dashboards',
                        text: dash,
                        html: true,
                        type: "warning",
                        showConfirmButton: true,
                        closeOnConfirm: true
                    });
                } else {
                    templateMangerData.deleteTemplate(id).then(function () {
                        _.remove(ctrl.templates, {id: id});
                    }, function (response) {
                        var msg = 'An error occurred while deleting the Template';
                        swal(msg);
                    });
                }
            });
        }

        //View template details
        function viewTemplateDetails(myitem) {
            ctrl.templateName = myitem.template;
            templateMangerData.search(myitem.template).then(function (response) {
                ctrl.templateDetails = response;
                $uibModal.open({
                    templateUrl: 'app/dashboard/views/templateDetails.html',
                    controller: 'TemplateDetailsController',
                    controllerAs: 'ctrl',
                    size: 'lg',
                    resolve: {
                        modalData: function () {
                            return {
                                templateDetails: ctrl.templateDetails
                            }
                        }
                    }
                });
            });
        }

        $scope.navigateToTab = function (tab) {
            $scope.tab = tab;
        }

        $scope.isActiveUser = function (user) {
            if (user.authType === ctrl.authType && user.username === ctrl.username) {
                return true;
            }
            return false;
        }

        $scope.promoteUserToAdmin = function (user) {
            userData.promoteUserToAdmin(user).then(
                function (response) {
                    var index = $scope.users.indexOf(user);
                    $scope.users[index] = response.data;
                },
                function (error) {
                    $scope.error = error;
                }
            );
        }

        $scope.demoteUserFromAdmin = function (user) {
            userData.demoteUserFromAdmin(user).then(
                function (response) {
                    var index = $scope.users.indexOf(user);
                    $scope.users[index] = response.data;
                },
                function (error) {
                    $scope.error = error;
                }
            );
        }
 
//Configuration settings functionality starts here
        dashboardData.getGeneralConfig().then(processGeneralConfigResponse);
        function processGeneralConfigResponse(data){
                $scope.choices = data;
        }

        $scope.oneAtATime = false;
        $scope.configTooltip = $sce.trustAsHtml("<ul class='tooltipList'><li>Url Should be the  server URL along with port, from where the jobs/appications have to be fetched.</li><li>Username Should be the functional username which has access to all the jobs/applications</li><li>Password Should be the corresponding password for the functional account</li></ul>");
        $scope.status = {
            isCustomHeaderOpen: true,
            open:true
        };
        $scope.showpassword = false;
        $scope.changePassIcon = function(){
            $scope.showpassword = !$scope.showpassword;
        }
        $scope.addNewConfig = function(objKey) {
            $scope.choices[objKey].info.push({}); 
        };

        $scope.removeNewConfig = function(item,objKey) {
            $scope.choices[objKey].info.splice(item,1);

        };
        $scope.removeConfigHead = function(e, objKey){
        e.preventDefault()
        $scope.choices.splice(objKey,1);
        }
        $scope.generalConfigFormSave =function(form, obj){

            if(form.$valid){
                  dashboardData.generalConfigSave(obj).then(
                    function(response) {
                        swal('Configuration saved successfully!!');
                    },
                    function(error) {
                        swal('Configuration not saved !!');
                    }
                );
           }else{
            $scope.status.open = true;
           }
        }
        $scope.togglePasswordType = function(idKey,classKey){
            var inputType = document.getElementById(idKey).type;
            if(inputType=="password"){
                document.getElementById(idKey).type="text";
                document.getElementById(classKey).className="fa fa-eye-slash";
            }else{
                document.getElementById(idKey).type="password";
                document.getElementById(classKey).className="fa fa-eye";                
            }
        }

        function filterByTitle (title) {
            var promises = paginationWrapperService.filterByTitle(title, ctrl.dashboardType);
            $q.all(promises).then (function() {
                ctrl.dashboards = paginationWrapperService.getDashboards();

            });
        }

    }
})();
