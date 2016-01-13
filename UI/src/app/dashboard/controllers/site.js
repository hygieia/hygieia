/**
 * Controller for choosing or creating a new dashboard
 */
(function () {
    'use strict';

    angular
        .module('devops-dashboard')
        .controller('SiteController', SiteController);

    SiteController.$inject = ['$scope', '$modal', 'dashboardData', '$location', '$cookies', '$cookieStore', '$timeout'];
    function SiteController($scope, $modal, dashboardData, $location, $cookies, $cookieStore, $timeout) {
        var ctrl = this;

        // public variables
        ctrl.search = '';
        ctrl.myadmin = '';
        ctrl.username=$cookies.username;
        ctrl.showAuthentication = $cookies.authenticated;
        ctrl.templateUrl = 'app/dashboard/views/navheader.html';

        ctrl.dashboardTypeEnum = {
            TEAM: 1,
            PRODUCT: 2
        };

        // public methods
        ctrl.createDashboard = createDashboard;
        ctrl.deleteDashboard = deleteDashboard;
        ctrl.open = open;
        ctrl.logout= logout;
        ctrl.admin = admin;
        ctrl.setType = setType;
        ctrl.filterNotOwnedList = filterNotOwnedList;
        ctrl.filterDashboards = filterDashboards;

        if (ctrl.username === 'admin') {
            ctrl.myadmin = true;
        }

        // request dashboards
        dashboardData.search().then(processDashboardResponse, processDashboardError);

        //find dashboard I own
        dashboardData.mydashboard(ctrl.username).then(processMyDashboardResponse, processMyDashboardError);

        dashboardData.types().then(function(response) {
            // add item icon
            response.forEach(function (item) {
                if(item.id == 2) {
                    item.icon = 'fa-cubes';
                }
            });

            ctrl.dashboardTypes = response;
        });

        function setType(type) {
            ctrl.dashboardType = type;
        }

        function filterDashboards(item) {
            return (!ctrl.search || item.name.toLowerCase().indexOf(ctrl.search.toLowerCase()) !== -1)
                && (!ctrl.dashboardType || item.type === ctrl.dashboardType);
        }

        function admin() {
            console.log('sending to admin page');
            $location.path('/admin');
        }

        function logout()
        {
            $cookieStore.remove("username");
            $cookieStore.remove("authenticated");
            $location.path("/");
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

        function processError() {
            alert("An error occurred");
        }

        function processDashboardResponse(data) {
            // add dashboards to list
            ctrl.dashboards = [];
            for (var x = 0; x < data.length; x++) {
                ctrl.dashboards.push({
                    id: data[x].id,
                    name: data[x].title,
                    type: data[x].type
                });
            }
        }

        function processDashboardError(data) {
            ctrl.dashboards = [];
        }

        function processMyDashboardResponse(mydata) {

            // add dashboards to list
            ctrl.mydash = [];
            for (var x = 0; x < mydata.length; x++) {

                ctrl.mydash.push({
                    id: mydata[x].id,
                    name: mydata[x].title,
                    type: mydata[x].type
                });

            }
        }

        function processMyDashboardError(data) {
            ctrl.mydash = [];
        }


        function deleteDashboard(id) {
            dashboardData.delete(id).then(function () {
                _.remove(ctrl.dashboards, {id: id});
                _.remove(ctrl.mydash, {id: id});
            }, function(response) {
                var msg = 'An error occurred';
                if (response.data && response.data.message) {
                    msg = response.data.message;
                }

                alert(msg);
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


