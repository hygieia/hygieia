/**
 * Gets feature related data
 */
(function() {
	'use strict';

	angular.module(HygieiaConfig.module + '.core').factory('featureData', featureData);

	function featureData($http) {
		var param = '?component=';
		var projectParam = '&projectId=';
		
		var agileType = {
			kanban : "&agileType=kanban",
			scrum : "&agileType=scrum",
		};
		var estimateMetricTypeParam = "&estimateMetricType=";
		var agileTypeParam = "&agileType=";
		
		var testAggregateSprintEstimates = 'test-data/feature-aggregate-sprint-estimates.json';
		var buildAggregateSprintEstimates = '/api/feature/estimates/aggregatedsprints/';

		var testFeatureWip = 'test-data/feature-super.json';
		var buildFeatureWip = '/api/feature/estimates/super/';

		var testSprint = 'test-data/feature-iteration.json';
		var buildSprint = '/api/iteration/';

		var testTeams = 'test-data/collector_type-scopeowner.json';
		var buildTeams = '/api/collector/item/type/ScopeOwner';

		var testTeamByCollectorItemId = 'test-data/collector_item-scopeowner.json';
		var buildTeamByCollectorItemId = '/api/collector/item/';
		
		var testProjectsRoute = 'test-data/projects.json';
        var buildProjectsRoute = '/api/scope';

		return {
			sprintMetrics : aggregateSprintEstimates,
			featureWip : featureWip,
			sprint : sprint,
			teams : teams,
			teamByCollectorItemId : teamByCollectorItemId,
			projects : projects
		};
		
		function aggregateSprintEstimates(componentId, filterTeamId, filterProjectId, estimateMetricType, agileType) {
			return $http.get(HygieiaConfig.local ? testAggregateSprintEstimates : buildAggregateSprintEstimates + filterTeamId + param + componentId + projectParam + filterProjectId
					+ (estimateMetricType != null? estimateMetricTypeParam + estimateMetricType : "")
					+ (agileType != null? agileTypeParam + agileType : ""))
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
		function featureWip(componentId, filterTeamId, filterProjectId, estimateMetricType, agileType) {
			return $http.get(HygieiaConfig.local ? testFeatureWip : buildFeatureWip + filterTeamId + param + componentId + projectParam + filterProjectId
					+ (estimateMetricType != null? estimateMetricTypeParam + estimateMetricType : "")
					+ (agileType != null? agileTypeParam + agileType : ""))
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
		function sprint(componentId, filterTeamId, filterProjectId, agileType) {
			return $http.get(HygieiaConfig.local ? testSprint : buildSprint + filterTeamId + param + componentId + projectParam + filterProjectId
					+ (agileType != null? agileTypeParam + agileType : ""))
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
		
		/**
         * Retrieves all projects
         */      
        function projects() {
            return $http.get(HygieiaConfig.local ? testProjectsRoute : (buildProjectsRoute))
                .then(function (response) {
                    return response.data;
                });
        }
	}
})();
