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


        //   ctrl.dashboards = []; //don't default since it's used to determine loading
        // ctrl.mydash = [];
        // public methods
        ctrl.createDashboard = createDashboard;
        ctrl.deleteDashboard = deleteDashboard;
        ctrl.open = open;
        ctrl.logout= logout;
        ctrl.admin = admin;
        ctrl.templateUrl = "app/dashboard/views/navheader.html";
        ctrl.filterNotOwnedList = filterNotOwnedList;



        if (ctrl.username === 'admin') {
            ctrl.myadmin = true;
        }



        // request dashboards
        dashboardData.search().then(processResponse);

        //find dashboard I own
        dashboardData.mydashboard(ctrl.username).then(processDashResponse);

        function admin() {
            console.log("sending to admin page");
            $location.path("/admin");
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

        function processResponse(data) {
            // add dashboards to list
            ctrl.dashboards = [];
            for (var x = 0; x < data.length; x++) {
                ctrl.dashboards.push({
                    id: data[x].id,
                    name: data[x].title
                });
            }
        }

        function processDashResponse(mydata) {
            // add dashboards to list
            ctrl.mydash = [];
            for (var x = 0; x < mydata.length; x++) {

                ctrl.mydash.push({
                    id: mydata[x].id,
                    name: mydata[x].title
                });

            }

        }


        function deleteDashboard(id) {
            dashboardData.delete(id).then(function () {
                _.remove(ctrl.dashboards, {id: id});
                _.remove(ctrl.mydash, {id: id});
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

        };



    }


})();


