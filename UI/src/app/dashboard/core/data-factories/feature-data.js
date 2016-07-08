/**
 * Gets feature related data
 */
(function() {
	'use strict';

	angular.module(HygieiaConfig.module + '.core').factory('featureData', featureData);

	function featureData($http) {
		var param = '?component=';
		var agileType = {
			kanban : "&agileType=kanban",
			scrum : "&agileType=scrum",
		};
		var estimateMetricTypeParam = "&estimateMetricType=";

		var testTotal = 'test-data/feature-total.json';
		var buildTotal = '/api/feature/estimates/total/';

		var testWip = 'test-data/feature-wip.json';
		var buildWip = '/api/feature/estimates/wip/';

		var testDone = 'test-data/feature-done.json';
		var buildDone = '/api/feature/estimates/done/';

		var testFeatureWip = 'test-data/feature-super.json';
		var buildFeatureWip = '/api/feature/estimates/super/';

		var testSprint = 'test-data/feature-iteration.json';
		var buildSprint = '/api/iteration/';

		var testTeams = 'test-data/collector_type-scopeowner.json';
		var buildTeams = '/api/collector/item/type/ScopeOwner';

		var testTeamByCollectorItemId = 'test-data/collector_item-scopeowner.json';
		var buildTeamByCollectorItemId = '/api/collector/item/';

		return {
			total : total,
			wip : wip,
			done : done,
			featureWip : featureWip,
			sprint : sprint,
			totalKanban : totalKanban,
			wipKanban : wipKanban,
			featureWipKanban : featureWipKanban,
			sprintKanban : sprintKanban,
			teams : teams,
			teamByCollectorItemId : teamByCollectorItemId
		};

		/**
		 * Retrieves total feature estimates for a given sprint and team
		 *
		 * @param componentId
		 * @param filterTeamId
		 */
		function total(componentId,filterTeamId,estimateMetricType) {
			return $http.get(HygieiaConfig.local ? testTotal : buildTotal + filterTeamId + param + componentId + agileType.scrum
					+ (estimateMetricType != null? estimateMetricTypeParam + estimateMetricType : ""))
					.then(function(response) {
						return response.data;
					});
		}

		/**
		 * Retrieves in-progress feature estimates for a given sprint and team
		 *
		 * @param componentId
		 * @param filterTeamId
		 */
		function wip(componentId,filterTeamId,estimateMetricType) {
			return $http.get(HygieiaConfig.local ? testWip : buildWip + filterTeamId + param + componentId + agileType.scrum
					+ (estimateMetricType != null? estimateMetricTypeParam + estimateMetricType : ""))
					.then(function(response) {
						return response.data;
					});
		}

		/**
		 * Retrieves done feature estimates for a given sprint and team
		 *
		 * @param componentId
		 * @param filterTeamId
		 */
		function done(componentId,filterTeamId,estimateMetricType) {
			return $http.get(HygieiaConfig.local ? testDone : buildDone + filterTeamId + param + componentId + agileType.scrum
					+ (estimateMetricType != null? estimateMetricTypeParam + estimateMetricType : ""))
					.then(function(response) {
						return response.data;
					});
		}

		/**
		 * Retrieves current super features and their total in progress
		 * estimates for a given sprint and team
		 *
		 * @param componentId
		 * @param filterTeamId
		 */
		function featureWip(componentId,filterTeamId,estimateMetricType) {
			return $http.get(HygieiaConfig.local ? testFeatureWip : buildFeatureWip + filterTeamId + param + componentId + agileType.scrum
					+ (estimateMetricType != null? estimateMetricTypeParam + estimateMetricType : ""))
					.then(function(response) {
						return response.data;
					});
		}

		/**
		 * Retrieves current team's sprint detail
		 *
		 * @param componentId
		 * @param filterTeamId
		 */
		function sprint(componentId,filterTeamId) {
			return $http.get(HygieiaConfig.local ? testSprint : buildSprint + filterTeamId + param + componentId + agileType.scrum)
					.then(function(response) {
						return response.data;
					});
		}

		/**
		 * Retrieves total feature estimates for a given sprint and team
		 * for kanban only
		 *
		 * @param componentId
		 * @param filterTeamId
		 */
		function totalKanban(componentId,filterTeamId,estimateMetricType) {
			return $http.get(HygieiaConfig.local ? testTotal : buildTotal + filterTeamId + param + componentId + agileType.kanban
					+ (estimateMetricType != null? estimateMetricTypeParam + estimateMetricType : ""))
					.then(function(response) {
						return response.data;
					});
		}

		/**
		 * Retrieves in-progress feature estimates for a given sprint and team
		 * for kanban only
		 *
		 * @param componentId
		 * @param filterTeamId
		 */
		function wipKanban(componentId,filterTeamId,estimateMetricType) {
			return $http.get(HygieiaConfig.local ? testWip : buildWip + filterTeamId + param + componentId + agileType.kanban
					+ (estimateMetricType != null? estimateMetricTypeParam + estimateMetricType : ""))
					.then(function(response) {
						return response.data;
					});
		}

		/**
		 * Retrieves current super features and their total in progress
		 * estimates for a given sprint and team for kanban only
		 *
		 * @param componentId
		 * @param filterTeamId
		 */
		function featureWipKanban(componentId,filterTeamId,estimateMetricType) {
			return $http.get(HygieiaConfig.local ? testFeatureWip : buildFeatureWip + filterTeamId + param + componentId + agileType.kanban
					+ (estimateMetricType != null? estimateMetricTypeParam + estimateMetricType : ""))
					.then(function(response) {
						return response.data;
					});
		}

		/**
		 * Retrieves current team's sprint detail for kanban only
		 *
		 * @param componentId
		 * @param filterTeamId
		 */
		function sprintKanban(componentId,filterTeamId) {
			return $http.get(HygieiaConfig.local ? testSprint : buildSprint + filterTeamId + param + componentId + agileType.kanban)
					.then(function(response) {
						return response.data;
					});
		}

		/**
		 * Retrieves all team names and team IDs
		 */
		function teams() {
			return $http.get(HygieiaConfig.local ? testTeams : buildTeams)
					.then(function(response) {
						return response.data;
					});
		}

		/**
		 * Retrieves a given team by its collector item ID
		 *
		 * @param collectorItemId
		 */
		function teamByCollectorItemId(collectorItemId) {
			return $http.get(HygieiaConfig.local ? testTeamByCollectorItemId : buildTeamByCollectorItemId + collectorItemId)
					.then(function(response) {
						return response.data;
					});
		}
	}
})();
