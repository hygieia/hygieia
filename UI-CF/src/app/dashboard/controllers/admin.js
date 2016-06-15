/**
 * Controller for administrative functionality
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('AdminController', AdminController);


    AdminController.$inject = ['dashboardData', '$cookies', '$cookieStore', '$location'];
    function AdminController(dashboardData, $cookies, $cookieStore, $location) {
        var ctrl = this;

        console.log("I am in admin page run scope");
        if ($cookies.username == 'admin') {
            console.log("I am admin");
            $location.path('/admin');

        }
        else {
            console.log("Not authenticated redirecting");
            $location.path('#');
        }

        ctrl.storageAvailable = localStorageSupported;
        ctrl.templateUrl = "app/dashboard/views/navheader.html";
        ctrl.username = $cookies.username;
        ctrl.logout = logout;

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


        //implementation of logout
        function logout() {
            $cookieStore.remove("username");
            $cookieStore.remove("authenticated");
            $location.path("/");
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

        function processResponse(data) {
            ctrl.dashboards = [];
            for (var x = 0; x < data.length; x++) {
                ctrl.dashboards.push({
                    id: data[x].id,
                    name: data[x].title
                });
            }
        }
    }
})();
