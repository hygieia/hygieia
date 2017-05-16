/**
 * Communicates with dashboard methods on the api
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .constant('DashboardType', {
            PRODUCT: 'product',
            TEAM: 'team'
        })
        .factory('dashboardData', dashboardData);

    function dashboardData($http) {
        var testSearchRoute = 'test-data/dashboard_search.json';
        var testDetailRoute = 'test-data/dashboard_detail.json';
        var testOwnedRoute='test-data/dashboard_owned.json';
        var testAllUsersRoute= 'test-data/all_users.json';
        var testOwnersRoute = 'test-data/owners.json';

        var dashboardRoute = '/api/dashboard';
        var mydashboardRoute = "/api/dashboard/mydashboard";
        var myownerRoute = "/api/dashboard/myowner";
        var dashboardAllUsersRoute= '/api/dashboard/allUsers';
        var dashboardOwnersRoute = '/api/dashboard/owners';

        return {
            search: search,
            mydashboard: mydashboard,
            myowner: myowner,
            allUsers: allUsers,
            owners: owners,
            promoteUserToOwner: promoteUserToOwner,
            demoteUserFromOwner: demoteUserFromOwner,
            detail: detail,
            create: create,
            delete: deleteDashboard,
            rename: renameDashboard,
            upsertWidget: upsertWidget,
            types: types,
            getComponent:getComponent
        };

        // reusable helper
        function getPromise(route) {
            return $http.get(route).then(function (response) {
                return response.data;
            });
        }

        // gets list of dashboards
        function search() {
            return getPromise(HygieiaConfig.local ? testSearchRoute : dashboardRoute);
        }

        //gets list of owned dashboard
        function mydashboard(username){
          return getPromise(HygieiaConfig.local ? testOwnedRoute : mydashboardRoute+ '/?username=' + username);
        }

        //gets dashboard owner from dashboard title
        function myowner(id)
        {
            return getPromise(HygieiaConfig.local ? testOwnedRoute : myownerRoute + "/" + id );
        }

        //gets component from componentId
        function getComponent(componentId){
            return getPromise(HygieiaConfig.local ? testOwnedRoute : myComponentRoute+ '/' + componentId);
        }

        function allUsers(id) {
            return getPromise(HygieiaConfig.local ? testAllUsersRoute : dashboardAllUsersRoute + "/" + id);
        }

        function owners(id) {
            return getPromise(HygieiaConfig.local ? testOwnersRoute : dashboardOwnersRoute + "/" + id);
        }

        // gets info for a single dashboard including available widgets
        function detail(id) {
            return getPromise(HygieiaConfig.local ? testDetailRoute : dashboardRoute + '/' + id);
        }

        // creates a new dashboard
        function create(data) {
            return $http.post(dashboardRoute, data)
                .success(function (response) {
                    return response.data;
                })
                .error(function (response) {
                    return null;
                });
        }


        // renames a dashboard

        function renameDashboard(id,newDashboardName){
            console.log("In data renaming dashboard");
            var postData= {
                title: newDashboardName
             }
            return $http.put(dashboardRoute+"/rename/"+id, postData)
                .success(
                    function (response) {
                    return response.data;
                })
                .error (function (response) {
                    console.log("Error Occured while renaming Dashboard in Data layer:"+JSON.stringify(response));
                    return response.data;
                });
        }

        function promoteUserToOwner(id, user) {
            var route = dashboardRoute + "/addOwner/"+id, user;
            return $http.put(route, user)
                .success(
                    function (response) {
                        return response.data;
                    })
                .error (function (response) {
                    console.log("Error Occured while promoting user to owner :"+JSON.stringify(response));
                    return response.data;
                });
        }

        function demoteUserFromOwner(id, user) {
            var route = dashboardRoute + "/removeOwner/"+id, user;
            return $http.put(route, user)
                .success(
                    function (response) {
                        return response.data;
                    })
                .error (function (response) {
                    console.log("Error Occured while demoting user from owner:"+JSON.stringify(response));
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

        function types() {
            return [
                {
                    "id": "team",
                    "name": "Team"
                },
                {
                    "id": "product",
                    "name": "Product"
                }
            ];

        }

        // can be used to add a new widget or update an existing one
        function upsertWidget(dashboardId, widget) {
            // create a copy so we don't modify the original
            widget = angular.copy(widget);

            console.log('New Widget Config', widget);

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
