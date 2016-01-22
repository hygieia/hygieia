/**
 * Controller for choosing or creating a new dashboard
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('SiteController', SiteController);

    SiteController.$inject = ['$scope', '$q', '$modal', 'dashboardData', '$location', '$cookies', '$cookieStore', 'DashboardType'];
    function SiteController($scope, $q, $modal, dashboardData, $location, $cookies, $cookieStore, DashboardType) {
        var ctrl = this;

        // public variables
        ctrl.search = '';
        ctrl.myadmin = '';
        ctrl.username = $cookies.username;
        ctrl.showAuthentication = $cookies.authenticated;
        ctrl.templateUrl = 'app/dashboard/views/navheader.html';
        ctrl.dashboardTypeEnum = DashboardType;

        // public methods
        ctrl.createDashboard = createDashboard;
        ctrl.deleteDashboard = deleteDashboard;
        ctrl.open = open;
        ctrl.logout = logout;
        ctrl.admin = admin;
        ctrl.setType = setType;
        ctrl.filterNotOwnedList = filterNotOwnedList;
        ctrl.filterDashboards = filterDashboards;

        if (ctrl.username === 'admin') {
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

            // request dashboards
            dashboardData.search().then(processDashboardResponse, processDashboardError);

            // request my dashboards
            dashboardData.mydashboard(ctrl.username).then(processMyDashboardResponse, processMyDashboardError);
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

        function logout()
        {
            $cookieStore.remove("username");
            $cookieStore.remove("authenticated");
            $location.path('/');
        }

        // method implementations
        function createDashboard() {
            // open modal for creating a new dashboard
            $modal.open({
                templateUrl: 'app/dashboard/views/createDashboard.html',
                controller: 'CreateDashboardController',
                controllerAs: 'ctrl'
            });
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
                    name: data[x].title,
                    isProduct: data[x].type && data[x].type.toLowerCase() === DashboardType.PRODUCT.toLowerCase()
                };

                if(board.isProduct) {
                    console.log(board);
                }
                dashboards.push(board);
            }

            ctrl.dashboards = dashboards;
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
                    name: mydata[x].title,
                    type: mydata[x].type,
                    isProduct: mydata[x].type && mydata[x].type.toLowerCase() === DashboardType.PRODUCT.toLowerCase()
                });
            }

            ctrl.mydash = dashboards;
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
    }


})();


