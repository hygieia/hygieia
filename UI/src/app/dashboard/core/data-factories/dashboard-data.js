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
        var testDetailCaponeRoute = 'test-data/dashboard_detail_capone.json';
        var testDetailCaponechatopsRoute = 'test-data/dashboard_detail_caponechatops.json';
        var testDetailCloudRoute = 'test-data/dashboard_detail_cloud.json';
        var testDetailSplitviewRoute = 'test-data/dashboard_detail_splitview.json';
        var testDetailProductRoute = 'test-data/dashboard_detail_product_dashboard.json';
        var testOwnedRoute='test-data/dashboard_owned.json';
        var testAllUsersRoute= 'test-data/all_users.json';
        var testGeneralConfigRoute= 'test-data/general_config.json';
        var testOwnersRoute = 'test-data/owners.json';

        var dashboardRoute = '/api/dashboard';
        var mydashboardRoute = "/api/dashboard/mydashboard";
        var myownerRoute = "/api/dashboard/myowner";
        var updateBusItemsRoute = '/api/dashboard/updateBusItems';
        var updateDashboardWidgetsRoute = '/api/dashboard/updateDashboardWidgets';
        var dashboardRoutePage = '/api/dashboard/page';
        var dashboardFilterRoutePage = '/api/dashboard/page/filter';
        var dashboardCountRoute = '/api/dashboard/count';
        var dashboardFilterCountRoute = '/api/dashboard/filter/count';
        var dashboardPageSize = '/api/dashboard/pagesize';
        var myDashboardRoutePage = '/api/dashboard/mydashboard/page';
        var myDashboardFilterRoutePage = '/api/dashboard/mydashboard/page/filter';
        var myDashboardCountRoute = '/api/dashboard/mydashboard/count';
        var myDashboardFilterCountRoute = '/api/dashboard/mydashboard/filter/count';
        var dashboardGenconfigRoute = '/api/dashboard/generalConfig';
        var updateDashboardScoreSettingsRoute = '/api/dashboard/updateScoreSettings';
        return {
            search: search,
            mydashboard: mydashboard,
            myowner: myowner,
            owners: owners,
            updateOwners: updateOwners,
            detail: detail,
            create: create,
            delete: deleteDashboard,
            rename: renameDashboard,
            upsertWidget: upsertWidget,
            types: types,
            getComponent:getComponent,
            updateBusItems:updateBusItems,
            updateDashboardWidgets:updateDashboardWidgets,
            deleteWidget:deleteWidget,
            searchByPage: searchByPage,
            filterByTitle:filterByTitle,
            count:count,
            filterCount: filterCount,
            getPageSize:getPageSize,
            myDashboardsCount:myDashboardsCount,
            searchMyDashboardsByPage:searchMyDashboardsByPage,
            filterMyDashboardsByTitle:filterMyDashboardsByTitle,
            filterMyDashboardCount:filterMyDashboardCount,
            getGeneralConfig: getGeneralConfig,
            generalConfigSave: generalConfigSave,
            updateDashboardScoreSettings: updateDashboardScoreSettings
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

        function owners(id) {
            return getPromise(HygieiaConfig.local ? testOwnersRoute : dashboardRoute + "/" + id + "/owners");
        }
        
        function updateOwners(id, owners) {
        	return $http.put(dashboardRoute + "/" + id + "/owners", owners).then(function (response) {
                return response.data;
            });
        }

        // gets info for a single dashboard including available widgets
        function detail(id) {
            if (HygieiaConfig.local) {
                var testDetailUrl;
                switch(id) {
                    case '01b819e203643678fd9343f8':
                        testDetailUrl = testDetailCaponeRoute;
                        break;
                    case '02b819e203643138fd9343f8':
                        testDetailUrl = testDetailCaponechatopsRoute;
                        break;
                    case '03b819e203643138fd9343f8':
                        testDetailUrl = testDetailCloudRoute;
                        break;
                    case '04b819e203643138fd934af8':
                        testDetailUrl = testDetailSplitviewRoute;
                        break;
                    case '05b819e20b643138fd9343f8':
                        testDetailUrl = testDetailProductRoute;
                        break;
                    default:
                        testDetailUrl = testDetailProductRoute;
                        break;
                }
                return getPromise(testDetailUrl);
            }
            return getPromise(dashboardRoute + '/' + id);
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

        function updateBusItems(id, data) {
            return $http.put(updateBusItemsRoute+"/"+id, data)
                .success(function (response) {
                    return response.data;
                })
                .error(function (response) {
                    return null;
                });
        }

        function updateDashboardWidgets(id, data) {
            return $http.put(updateDashboardWidgetsRoute + "/" + id, data)
                .success(function (response) {
                    return response.data;
                })
                .error(function (response) {
                    return null;
                });
        }

        // can be used to delete existing widget
        function deleteWidget(dashboardId, widget) {
            widget = angular.copy(widget);
            console.log('Delete widget config', widget);
            var widgetId = widget.id;
            if (widgetId) {
                // remove the id since that would cause an api failure
                delete widget.id;
            }
            var route = $http.put(dashboardRoute + '/' + dashboardId + '/deleteWidget/' + widgetId, widget) ;
            return route.success(function (response) {
                return response.data;
            }).error(function (response) {
                return null;
            });

        }

        // gets count of all dashboards
        function count(type) {
            return getPromise(HygieiaConfig.local ? testSearchRoute : dashboardCountRoute+ '/'+type);
        }

        // gets list of dashboards according to page size (default = 10)
        function searchByPage(params) {
            return  $http.get(HygieiaConfig.local ? testSearchRoute : dashboardRoutePage,{params: params}).then(function (response) {
                return {"data": response.data, "type": params.type};
            });
        }

        // gets list of dashboards filtered by title with page size (default = 10)
        function filterByTitle(params) {
            return  $http.get(HygieiaConfig.local ? testSearchRoute : dashboardFilterRoutePage,{params: params}).then(function (response) {
                return {"data": response.data, "type": params.type};
            });
        }

        //gets count of filtered dashboards for pagination
        function filterCount(title, type){
            return  $http.get(HygieiaConfig.local ? testSearchRoute : dashboardFilterCountRoute+ '/'+title+ '/'+type).then(function (response) {
                return response.data;
            });
        }

        // gets page size
        function getPageSize() {
            return getPromise(HygieiaConfig.local ? testSearchRoute : dashboardPageSize);
        }

        // gets count of all my dashboards
        function myDashboardsCount(type) {
            return getPromise(HygieiaConfig.local ? testSearchRoute : myDashboardCountRoute+ '/'+type);
        }

        // gets list of my dashboards according to page size (default = 10)
        function searchMyDashboardsByPage(params) {
            return  $http.get(HygieiaConfig.local ? testSearchRoute : myDashboardRoutePage,{params: params}).then(function (response) {
                return {"data": response.data, "type": params.type};
            });
        }

        // gets list of my dashboards filtered by title with page size (default = 10)
        function filterMyDashboardsByTitle(params) {
            return  $http.get(HygieiaConfig.local ? testSearchRoute : myDashboardFilterRoutePage,{params: params}).then(function (response) {
                return {"data": response.data, "type": params.type};
            });
        }

        //gets count of filtered dashboards for pagination
        function filterMyDashboardCount(title, type){
            return  $http.get(HygieiaConfig.local ? testSearchRoute : myDashboardFilterCountRoute+ '/'+title+ '/'+type).then(function (response) {
                return response.data;
            });
        }

        //get List of all configurations
        function getGeneralConfig(id) {
            return getPromise(HygieiaConfig.local ? testGeneralConfigRoute : dashboardGenconfigRoute+'/fetch');
        }
        //To save the general config datas
        function generalConfigSave(obj){
            var route = dashboardGenconfigRoute, obj;
            return $http.put(route, obj)
                .success(
                    function (response) {
                        return response.data;
                    })
                .error (function (response) {
                    console.log("Error Occured while saving the configuration:"+JSON.stringify(response));
                    return response.data;
                });
        }

        function updateDashboardScoreSettings(id, scoreEnabled, scoreDisplay) {
            return $http.put(updateDashboardScoreSettingsRoute + "/" + id + "?scoreEnabled=" + scoreEnabled + "&scoreDisplay=" + scoreDisplay)
                .success(function (response) {
                    return response.data;
                })
                .error(function (response) {
                    return null;
                });
        }

    }
})();
