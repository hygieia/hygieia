(function () {
	'use strict';

	angular
		.module(HygieiaConfig.module)
		.controller('wlmonitorViewController', wlmonitorViewController);

	wlmonitorViewController.$inject = ['$scope', 'wlmonitorData', 'DashStatus', '$uibModal', '$q','collectorData','$route'];
	function wlmonitorViewController($scope, wlmonitorData, DashStatus, $uibModal, $q,collectorData,$route) {

		var ctrl = this;

		var dashboardId = $route.current.params.id;

		ctrl.environments = [];
		ctrl.statuses = DashStatus;

		ctrl.load = load;
		ctrl.showDetail = showDetail;
		ctrl.selectedList =[];


		function load() {
			var deferred = $q.defer();
			collectorData.itemsByType('WLMonitor').then(function(data) {
				console.log(data)
				ctrl.selectedList = _.filter(data, function(val){ return (val.enabled == true)});
				angular.forEach(ctrl.selectedList, function(list, key) {
					var env = list.description;
					wlmonitorData.details(env).then(function(data1) {
						list.dataList = data1;
						console.log(list.dataList);
						list.serverUpCount = _(data1).where({'state':"RUNNING"}).value().length;
						list.serverDownCount = list.dataList.length - list.serverUpCount;
						list.failedComponents = list.serverDownCount;
						// _(value).where({'state':"FAILURE"}).value().length;
					});
				});
				deferred.resolve(data.lastUpdated);
			});
			return deferred.promise;
		}


		function showDetail(env) {

			console.log(env);
			var environment = env.dataList;
			$uibModal.open({
				controller: 'wlmonitorDetailController',
				controllerAs: 'detail',
				templateUrl: 'components/widgets/wlmonitor/detail.html',
				size: 'lg',
				resolve: {
					environment: function() {
						return environment;
					},
					collectorName: function () {
						return ''
					}
				}
			});
		}


		function processResponse(data) {
			console.log(data);
		}

		function defaultStateCallback(isDefaultState) {
			$scope.display = isDefaultState ? DisplayState.DEFAULT : DisplayState.ERROR;
		}

		function environmentsCallback(data) {
			ctrl.environments = data.environments;
		}
	}
})();
