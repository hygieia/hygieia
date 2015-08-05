/**
 * Communicates with dashboard methods on the api
 */
(function () {
    'use strict';

    angular
        .module('devops-dashboard.core')
        .factory('dashboardData', dashboardData);

    function dashboardData($http) {
        var testSearchRoute = 'test-data/dashboard_search.json';
        var testDetailRoute = 'test-data/dashboard_detail.json';
        var testOwnedRoute='test-data/dashboard_owned.json';

        var dashboardRoute = '/api/dashboard';
        var mydashboardRoute = "/api/dashboard/mydashboard";
        var myownerRoute = "/api/dashboard/myowner";

        return {
            search: search,
            mydashboard: mydashboard,
            myowner: myowner,
            detail: detail,
            create: create,
            delete: deleteDashboard,
            upsertWidget: upsertWidget
        };

        // reusable helper
        function getPromise(route) {
            return $http.get(route).then(function (response) {
                return response.data;
            });
        }

        // gets list of dashboards
        function search() {
            return getPromise(localTesting ? testSearchRoute : dashboardRoute);
        }

        //gets list of owned dashboard
        function mydashboard(username){
          return getPromise(localTesting ? testOwnedRoute : mydashboardRoute+ "/" + username);
        }

        //gets dashboard owner from dashboard titile
        function myowner(title)
        {
            return getPromise(localTesting ? testOwnedRoute : myownerRoute + "/" + title );
        }

        // gets info for a single dashboard including available widgets
        function detail(id) {
            return getPromise(localTesting ? testDetailRoute : dashboardRoute + '/' + id);
        }

        // creates a new dashboard
        function create(data) {
            return $http.post(dashboardRoute, data)
                .then(function (response) {
                    return response.data;
                });
        }

        // deletes a dashboard
        function deleteDashboard(id) {
            return $http.delete(dashboardRoute + '/' + id)
                .then(function (response) {
                    return response.data;
            });
        }

        // can be used to add a new widget or update an existing one
        function upsertWidget(dashboardId, widget) {
            // create a copy so we don't modify the original
            widget = angular.copy(widget);

            var widgetId = widget.id;

            if (widgetId) {
                // remove the id since that would cause an api failure
                delete widget.id;
            }

            var route = widgetId ?
                $http.put(dashboardRoute + '/' + dashboardId + '/widget/' + widgetId, widget) :
                $http.post(dashboardRoute + '/' + dashboardId + '/widget', widget);

            return route.then(function (response) {
                return response.data;
            });
        }
    }
})();
