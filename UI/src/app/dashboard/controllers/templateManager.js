/**
 * Controller for the template CRUD page
 */
(function() {
	'use strict';

	angular.module(HygieiaConfig.module)
            .controller('TemplateController', TemplateController);

	TemplateController.$inject = [ '$scope', '$location', '$cookies', '$cookieStore', 'widgetManager', 'DashboardType', 'dashboardData', '$modal' ];
	function TemplateController($scope, $location, $cookies, $cookieStore, widgetManager, DashboardType, dashboardData, $modal) {
        var ctrl = this;

        // public variables
        ctrl.search = '';
        ctrl.myadmin = '';
        ctrl.username = $cookies.username;
        ctrl.showAuthentication = $cookies.authenticated;
        ctrl.navUrl = 'app/dashboard/views/navheader.html';
        ctrl.dashboardTypeEnum = DashboardType;

        // public methods
        ctrl.createTemplate = createTemplate;
        ctrl.goToManager = goToManager;
        ctrl.logout = logout;
        ctrl.admin = admin;
        ctrl.filterTemplates = filterTemplates;

        ctrl.toggleWidget = toggleWidget;
        ctrl.removeWidget = removeWidget;
        ctrl.onChange = onChange;
        ctrl.onDragStart = onDragStart;
        ctrl.onResizeStart = onResizeStart;
        ctrl.onResizeStop = onResizeStop;
        ctrl.clearWidgets = clearWidgets;

        if (ctrl.username === 'admin') {
            ctrl.myadmin = true;
        }

        $scope.widgets = {};
        ctrl.widgets = widgetManager.getWidgets();
        
        $scope.options = {
            cellHeight: 200,
            verticalMargin: 10
        };

        function toggleWidget(widget, $event) {
            if (widget in $scope.widgets) removeWidget(widget);
            else addWidget(widget);
            
            $event.target.classList.toggle("added");
        }

        function addWidget(widgetTitle) {
            var newWidget = { x:0, y:0, width:3, height:1 };
            $scope.widgets[widgetTitle] = newWidget;
        };

        function removeWidget(title, $event = null) {
            if ($event != null) document.getElementById(title + '-button').classList.remove('added');
            delete $scope.widgets[title];
        };

        function clearWidgets($event) {
            for (var title in $scope.widgets) {
                removeWidget(title, $event);
            }
        }

        function onChange(event, items) {
            console.log("onChange event: "+event+" items:"+items);
        };

        function onDragStart(event, ui) {
            console.log("onDragStart event: "+event+" ui:"+ui);
        };

        function onDragStop(event, ui) {
            console.log("onDragStop event: "+event+" ui:"+ui);
        };

        function onResizeStart(event, ui) {
            console.log("onResizeStart event: "+event+" ui:"+ui);
        };

        function onResizeStop(event, ui) {
            console.log("onResizeStop event: "+event+" ui:"+ui);
        };

        function filterTemplates(item) {
            var matchesSearch = (!ctrl.search || item.name.toLowerCase().indexOf(ctrl.search.toLowerCase()) !== -1);
            if (ctrl.templateType == DashboardType.PRODUCT) {Template
                return !item.isProduct && matchesSearch;
            }

            return matchesSearch;
        }

        function admin() {
            console.log('sending to admin page');
            $location.path('/admin');
        }

        function logout() {
            $cookieStore.remove("username");
            $cookieStore.remove("authenticated");
            $location.path('/');
        }

        // method implementations
        function createTemplate() {
            $modal.open({
                templateUrl: 'app/dashboard/views/createDashboard.html',
                controller: 'CreateDashboardController',
                controllerAs: 'ctrl'
            });
        }

        function goToManager() {
            $location.path('/templates/create');
        }
    }
})();