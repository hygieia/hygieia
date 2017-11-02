/**
 * Controller for administrative functionality
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('AdminController', AdminController);


    AdminController.$inject = ['$scope', 'dashboardData', '$location', '$uibModal', 'userService', 'authService', 'userData', 'dashboardService', 'templateMangerData', 'gamificationMetricData'];
    function AdminController($scope, dashboardData, $location, $uibModal, userService, authService, userData, dashboardService, templateMangerData, gamificationMetricData) {
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
        ctrl.deleteMetricRange = deleteMetricRange;
        ctrl.addMetricRange = addMetricRange;
        ctrl.saveMetricData = saveMetrics;
        ctrl.validateScoringRanges = validateScoringRanges;

        $scope.tab = "dashboards";

        ctrl.metricList = [
            {
                metricName: "codeCoverage",
                formattedName: "Code Coverage",
                gamificationScoringRanges: [],
                enabled: false,
                description: "",
                symbol: ""
            },
            {
                metricName: "unitTests",
                formattedName: "Unit Test Success",
                gamificationScoringRanges: [],
                enabled: false,
                description: "",
                symbol: ""
            },
            {
                metricName: "buildSuccess",
                formattedName: "Build Success",
                gamificationScoringRanges: [],
                enabled: false,
                description: "",
                symbol: ""
            },
            {
                metricName: "codeViolations",
                formattedName: "Code Violations",
                gamificationScoringRanges: [],
                enabled: false,
                description: "",
                symbol: ""
            }
        ];

        // var testData = {
        //     metricName: "codeCoverage2",
        //     formattedName: "Code Coverage",
        //     gamificationScoringRanges: [{ min: 0, max: 50, score: 0 },
        //         { min: 51, max: 60, score: 4 },
        //         { min: 61, max: 70, score: 8 },
        //         { min: 71, max: 80, score: 12 },
        //         { min: 81, max: 90, score: 16 },
        //         { min: 91, max: 95, score: 18 }],
        //     enabled: false
        //
        // };

        ctrl.metricData = [];
        $scope.selectedMetric = null;

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


        // request dashboards
        dashboardData.search().then(processResponse);
        userData.getAllUsers().then(processUserResponse);
        userData.apitokens().then(processTokenResponse);
        templateMangerData.getAllTemplates().then(processTemplateResponse);
        gamificationMetricData.getMetricData().then(processMetricResponse);
        // gamificationMetricData.storeMetricData(testData).then(saveMetrics);


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
        function editToken(item)
        {
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

        function processResponse(data) {
            ctrl.dashboards = [];
            for (var x = 0; x < data.length; x++) {
                ctrl.dashboards.push({
                    id: data[x].id,
                    name: dashboardService.getDashboardTitle(data[x]),
                    type: data[x].type,
                    validServiceName: data[x].validServiceName,
                    validAppName: data[x].validAppName,
                    configurationItemBusServName: data[x].configurationItemBusServName,
                    configurationItemBusAppName: data[x].configurationItemBusAppName,
                });
            }
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

        function processMetricResponse(response) {
            console.log(response.data);
            var data = response.data;

            ctrl.metricList.forEach(function(metric) {
                // Check if metric exists in db already
                data.forEach(function(entry) {
                   if (metric.metricName === entry.metricName) {
                       metric.enabled = entry.enabled;
                       metric.gamificationScoringRanges = entry.gamificationScoringRanges;
                   }
                });

                ctrl.metricData.push(metric);
            });

            console.log(ctrl.metricData);
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

        function deleteMetricRange(sel) {
            var idx = -1;
            $scope.selectedMetric.gamificationScoringRanges.forEach(function(range, i) {
                if (sel.min === range.min && sel.max == range.max && sel.score === range.score)
                    idx = i;
            });

            $scope.selectedMetric.gamificationScoringRanges.splice(idx, 1);
        }

        function addMetricRange() {
            $scope.selectedMetric.gamificationScoringRanges.push({min: 0, max: 0, score: 0});
        }

        function saveMetrics() {
            if($scope.selectedMetric != undefined) {
                var isValidationSuccessful = ctrl.validateScoringRanges($scope.selectedMetric.gamificationScoringRanges);
                if(isValidationSuccessful) {
                    gamificationMetricData.storeMetricData($scope.selectedMetric).then(validatePost);
                } else {
                    console.log("Validation failed for the scoring ranges. Fix the ranges and click Save again");
                }
            }
        }

        function validateScoringRanges(gamificationScoringRanges) {
            var prevMax = null;
            var prevMin = null;
            if(gamificationScoringRanges.length == 0) {
                console.log("Atleast one range needs to be added to save.");
                return false;
            }
            var isValidationSuccessful = true;
            var ValidationException = {};
            try {
                gamificationScoringRanges.forEach(function(range, i) {
                    if(i > 0) {
                        if(prevMin == range.min && prevMax == range.max) {
                            console.log("Duplicates detected in the scoring ranges.");
                            throw ValidationException;
                        }
                        if(range.min <= prevMax || range.min - prevMax > 1) {
                            console.log("Overlap and/or gaps detected in the scoring ranges.");
                            throw ValidationException;
                        }
                    }
                    if(range.min > range.max) {
                        console.log("Min should be less than the max in a scoring range.");
                        throw ValidationException;
                    }
                    prevMin = range.min;
                    prevMax = range.max;
                });
            } catch (e) {
                isValidationSuccessful = false;
                if(e != ValidationException) throw e;
            }
            return isValidationSuccessful;
        }

        function validatePost(response) {
            console.log(response);
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

        $scope.switchMetric = function(metricName) {
            ctrl.metricData.forEach(function(obj, idx) {
                if (obj.metricName === metricName)
                    $scope.selectedMetric = ctrl.metricData[idx];
            });
        }
    }
})();
