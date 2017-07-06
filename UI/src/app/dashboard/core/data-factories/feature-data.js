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
		
		var testProjectsRoute = 'test-data/projects.json';
        var buildProjectsRoute = '/api/scope';

		var testProjectsByCollectorId = 'test-data/teams.json';
		var buildProjectsByCollectorId = '/api/scopecollector/';
		var buildProjectsByCollectorIdPage = '/api/scopecollector/page/';

		var testTeamsRoute = 'test-data/teams.json';
		var buildTeamsRoute = '/api/team';

		var testTeamsByCollectorId = 'test-data/teams.json';
		var buildTeamsByCollectorId = '/api/teamcollector/';
		var buildTeamsByCollectorIdPage = '/api/teamcollector/page/';

		return {
			sprintMetrics : aggregateSprintEstimates,
			featureWip : featureWip,
			sprint : sprint,
			teams : teams,
			teamsByCollectorId : teamsByCollectorId,
			projects : projects,
			projectsByCollectorId : projectsByCollectorId,
			projectsByCollectorIdPaginated:projectsByCollectorIdPaginated,
			teamsByCollectorIdPaginated:teamsByCollectorIdPaginated
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
		 * Retrieves projects by  collector ID
		 *
		 * @param collectorId
		 */
		function projectsByCollectorId(collectorId) {
			return $http.get(HygieiaConfig.local ? testProjectsByCollectorId : buildProjectsByCollectorId + collectorId)
				.then(function(response) {
					return response.data;
				});
		}

		/**
		 * Retrieves projects by  collector ID
		 *
		 * @param collectorId
		 */
		function projectsByCollectorIdPaginated(collectorId,params) {
			return $http.get(HygieiaConfig.local ? testProjectsByCollectorId : buildProjectsByCollectorIdPage + collectorId,{params: params})
				.then(function(response) {
					return response.data;
				});
		}


		/**
		 * Retrieves teams by  collector ID
		 *
		 * @param collectorId
		 */
		function teamsByCollectorId(collectorId) {
			return $http.get(HygieiaConfig.local ? testTeamsByCollectorId : buildTeamsByCollectorId + collectorId)
					.then(function(response) {
						return response.data;
					});
		}

		/**
		 * Retrieves teams by  collector ID
		 *
		 * @param collectorId
		 */
		function teamsByCollectorIdPaginated(collectorId,params) {
			return $http.get(HygieiaConfig.local ? testTeamsByCollectorId : buildTeamsByCollectorIdPage + collectorId,{params: params})
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

		/**
		 * Retrieves all teams
		 */
		function teams() {
			return $http.get(HygieiaConfig.local ? testTeamsRoute : (buildTeamsRoute))
				.then(function (response) {
					return response.data;
				});
		}
	}
})();
