/**
 * Controller for choosing or creating a new dashboard
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('SiteController', SiteController);

    SiteController.$inject = ['$scope', '$q', '$uibModal', 'dashboardData', '$location', 'DashboardType', 'userService', 'authService','dashboardService', 'user'];
    function SiteController($scope, $q, $uibModal, dashboardData, $location, DashboardType, userService, authService, dashboardService, user) {
        var ctrl = this;

        // public variables
        ctrl.search = '';
        ctrl.myadmin = '';

        ctrl.username = userService.getUsername();
        ctrl.showAuthentication = userService.isAuthenticated();

        ctrl.templateUrl = 'app/dashboard/views/navheader.html';
        ctrl.dashboardTypeEnum = DashboardType;

        // pagination variables
        $scope.currentPage = 0;
        ctrl.searchFilter="";

        // pagination variables
        $scope.currentPageMyDash = 0;

        // public methods
        ctrl.createDashboard = createDashboard;
        ctrl.deleteDashboard = deleteDashboard;
        ctrl.manageTemplates = manageTemplates;
        ctrl.open = open;
        ctrl.login = login;
        ctrl.logout = logout;
        ctrl.admin = admin;
        ctrl.setType = setType;
        ctrl.filterNotOwnedList = filterNotOwnedList;
        ctrl.filterDashboards = filterDashboards;
        ctrl.editDashboard = editDashboard;
        ctrl.getInvalidAppOrCompError = getInvalidAppOrCompError;
        ctrl.pageChangeHandler = pageChangeHandler;
        ctrl.pageChangeHandlerForMyDash = pageChangeHandlerForMyDash;
        ctrl.filterByTitle = filterByTitle;

        if (userService.isAdmin()) {
            ctrl.myadmin = true;
        }

        (function() {
            // set up the different types of dashboards with a custom icon
            var types = dashboardData.types();
            _(types).forEach(function (item) {
                if(item.id == DashboardType.PRODUCT) {
                    item.icon = 'fa-cubes';
                }
            });

            ctrl.dashboardTypes = types;


            dashboardData.getPageSize().then(function (data) {
                 if(data!=null && data>0){
                    $scope.pageSize = data;
                }
                pullDashboards();
            });

        })();

        function setType(type) {
            ctrl.dashboardType = type;
        }

        function filterDashboards(item) {
            var matchesSearch = (!ctrl.search || item.name.toLowerCase().indexOf(ctrl.search.toLowerCase()) !== -1);
            if (ctrl.dashboardType == DashboardType.PRODUCT) {
                return item.isProduct && matchesSearch;
            }

            if (ctrl.dashboardType == DashboardType.TEAM) {
                return !item.isProduct && matchesSearch;
            }

            return matchesSearch;
        }

        function admin() {
            console.log('sending to admin page');
            $location.path('/admin');
        }

        function login() {
          $location.path('/login');
        }

        function logout() {
            authService.logout();
            $location.path('/login');
        }

        // method implementations
        function createDashboard() {
            // open modal for creating a new dashboard
            $uibModal.open({
                templateUrl: 'app/dashboard/views/createDashboard.html',
                controller: 'CreateDashboardController',
                controllerAs: 'ctrl'
            });
        }

        function editDashboard(item,size)
        {
            // open modal for renaming dashboard
            var modalInstance = $uibModal.open({
                templateUrl: 'app/dashboard/views/editDashboard.html',
                controller: 'EditDashboardController',
                controllerAs: 'ctrl',
                size:size,
                resolve: {
                    dashboardItem: function() {
                        return item;
                    }
                }
            });
            modalInstance.result.then(function success() {
                pullDashboards()
            });
        }

        function manageTemplates() {
            $location.path('/templates');
        }

        function open(dashboardId) {
            $location.path('/dashboard/' + dashboardId);
        }

        function processDashboardResponse(data) {
            // add dashboards to list
            ctrl.dashboards = [];
            var dashboards = [];
            for (var x = 0; x < data.length; x++) {
                var board = {
                    id: data[x].id,
                    name: dashboardService.getDashboardTitle(data[x]),
                    isProduct: data[x].type && data[x].type.toLowerCase() === DashboardType.PRODUCT.toLowerCase()
                };

                if(board.isProduct) {
                    //console.log(board);
                }
                dashboards.push(board);
            }

            ctrl.dashboards = dashboards;
            dashboardData.count().then(function (data) {
                ctrl.totalItems = data;
            });
        }

        function processDashboardFilterResponse(data) {
            ctrl.dashboards = [];
            var dashboards = [];
            for (var x = 0; x < data.length; x++) {
                var board = {
                    id: data[x].id,
                    name: dashboardService.getDashboardTitle(data[x]),
                    isProduct: data[x].type && data[x].type.toLowerCase() === DashboardType.PRODUCT.toLowerCase()
                };
                if(board.isProduct) {
                    //console.log(board);
                }
                dashboards.push(board);
            }
            ctrl.dashboards = dashboards;
            if(ctrl.searchFilter==""){
                dashboardData.count().then(function (data) {
                    ctrl.totalItems = data;
                });
            }
        }

        function processDashboardError(data) {
            ctrl.dashboards = [];
        }

        function processMyDashboardResponse(mydata) {

            // add dashboards to list
            ctrl.mydash = [];
            var dashboards = [];
            for (var x = 0; x < mydata.length; x++) {

                dashboards.push({
                    id: mydata[x].id,
                    name: dashboardService.getDashboardTitle(mydata[x]),
                    type: mydata[x].type,
                    isProduct: mydata[x].type && mydata[x].type.toLowerCase() === DashboardType.PRODUCT.toLowerCase(),
                    validServiceName:  mydata[x].validServiceName,
                    validAppName: mydata[x].validAppName,
                    configurationItemBusServName:  mydata[x].configurationItemBusServName,
                    configurationItemBusAppName:  mydata[x].configurationItemBusAppName,
                    configurationItemBusServId:  mydata[x].configurationItemBusServObjectId,
                    configurationItemBusAppId:  mydata[x].configurationItemBusAppObjectId,
                    showError: ctrl.getInvalidAppOrCompError(mydata[x])
                });
            }

            ctrl.mydash = dashboards;
            dashboardData.myDashboardsCount().then(function (data) {
                ctrl.totalItemsMyDash = data;
            });
        }

        function processFilterMyDashboardResponse(mydata) {

            // add dashboards to list
            ctrl.mydash = [];
            var dashboards = [];
            for (var x = 0; x < mydata.length; x++) {

                dashboards.push({
                    id: mydata[x].id,
                    name: dashboardService.getDashboardTitle(mydata[x]),
                    type: mydata[x].type,
                    isProduct: mydata[x].type && mydata[x].type.toLowerCase() === DashboardType.PRODUCT.toLowerCase(),
                    validServiceName:  mydata[x].validServiceName,
                    validAppName: mydata[x].validAppName,
                    configurationItemBusServName:  mydata[x].configurationItemBusServName,
                    configurationItemBusAppName:  mydata[x].configurationItemBusAppName,
                    configurationItemBusServId:  mydata[x].configurationItemBusServObjectId,
                    configurationItemBusAppId:  mydata[x].configurationItemBusAppObjectId,
                    showError: ctrl.getInvalidAppOrCompError(mydata[x])
                });
            }

            ctrl.mydash = dashboards;
            if(ctrl.searchFilter=="") {
                dashboardData.myDashboardsCount().then(function (data) {
                    ctrl.totalItemsMyDash = data;
                });
            }
        }


        function processMyDashboardError(data) {
            ctrl.mydash = [];
        }




        function deleteDashboard(item) {
            var id = item.id;
            dashboardData.delete(id).then(function () {
                _.remove(ctrl.dashboards, {id: id});
                _.remove(ctrl.mydash, {id: id});
            }, function(response) {
                var msg = 'An error occurred while deleting the dashboard';

                if(response.status > 204 && response.status < 500) {
                    msg = 'The Team Dashboard is currently being used by a Product Dashboard/s. You cannot delete at this time.';
                }

                swal(msg);
            });
        }

        function filterNotOwnedList(db1, db2) {

            console.log("size before is:" + db1.length);

            var jointArray = db1.concat(db2);

            console.log("size after is:" + jointArray.length);

            var uniqueArray = jointArray.filter(function (elem, pos) {
                return jointArray.indexOf(elem) == pos;
            });

            console.log("size after reduction  is:" + uniqueArray.length);
            ctrl.dashboards = uniqueArray;
        }
        function getInvalidAppOrCompError(data){
            var showError = false;

            if((data.configurationItemBusServName != undefined && !data.validServiceName) || (data.configurationItemBusAppName != undefined && !data.validAppName)){
                showError = true;
            }
            return showError;
        }
        function pullDashboards(){
            // request dashboards
            dashboardData.searchByPage({"search": '', "size": $scope.pageSize, "page": 0}).then(processDashboardResponse, processDashboardError);

            // request my dashboards
            dashboardData.searchMyDashboardsByPage({"username": ctrl.username, "size": $scope.pageSize, "page": 0}).then(processMyDashboardResponse, processMyDashboardError);

            dashboardData.count().then(function (data) {
                ctrl.totalItems = data;
            });
            dashboardData.myDashboardsCount().then(function (data) {
               ctrl.totalItemsMyDash = data;
            });
        }

        function pageChangeHandler(pageNumber){
            if(ctrl.searchFilter==""){
                dashboardData.searchByPage({"search": '', "size": $scope.pageSize, "page": pageNumber-1}).then(processDashboardResponse, processDashboardError);
            }else{
                dashboardData.filterByTitle({"search": ctrl.searchFilter, "size": $scope.pageSize, "page": pageNumber-1}).then(processDashboardFilterResponse, processDashboardError);
            }
            $scope.currentPage = pageNumber;
        }

        function pageChangeHandlerForMyDash(pageNumber){
            if(ctrl.searchFilter==""){
                dashboardData.searchMyDashboardsByPage({"username": ctrl.username, "size": $scope.pageSize, "page": pageNumber-1}).then(processMyDashboardResponse, processMyDashboardError);
            }else{
                dashboardData.filterMyDashboardsByTitle({"search":  ctrl.searchFilter, "size": $scope.pageSize, "page": pageNumber-1}).then(processFilterMyDashboardResponse, processMyDashboardError);
            }
            $scope.currentPageMyDash = pageNumber;
        }


        function filterByTitle(title){
            $scope.currentPage = 0;
            $scope.currentPageMyDash = 0;
            ctrl.searchFilter = title;
            if(title==""){
                dashboardData.searchByPage({"search": '', "size": $scope.pageSize, "page": 0}).then(processDashboardResponse, processDashboardError);
                dashboardData.searchMyDashboardsByPage({"username": ctrl.username, "size": $scope.pageSize, "page": 0}).then(processMyDashboardResponse, processMyDashboardError);
            }else{
                dashboardData.filterCount(title).then(function (data) {
                    ctrl.totalItems = data;
                });
                dashboardData.filterByTitle({"search": title, "size": $scope.pageSize, "page": 0}).then(processDashboardFilterResponse, processDashboardError);

                dashboardData.filterMyDashboardCount(title).then(function (data) {
                    ctrl.totalItemsMyDash = data;
                });
                dashboardData.filterMyDashboardsByTitle({"search": title, "size": $scope.pageSize, "page": 0}).then(processFilterMyDashboardResponse, processMyDashboardError);

            }
        }
    }


})();
