(function() {
	'use strict';

	angular.module(HygieiaConfig.module).controller('featureConfigController',
			featureConfigController);

	featureConfigController.$inject = [ 'modalData', '$uibModalInstance',
			'collectorData', 'featureData' ];

	function featureConfigController(modalData, $uibModalInstance, collectorData, featureData) {
		/* jshint validthis:true */
		var ctrl = this;
		var widgetConfig = modalData.widgetConfig;

		// public state change variables
		ctrl.projectsDropdownPlaceholder = 'Loading Projects ...';
		ctrl.projectsDropdownDisabled = true;
		ctrl.teamsDropdownPlaceholder = 'Loading Teams ...';
		ctrl.teamsDropdownDisabled = true;
		ctrl.typeDropdownPlaceholder = 'Loading Feature Data Sources ...';
		ctrl.typeDropdownDisabled = true;
		ctrl.estimateMetricDropdownDisabled = false;
		ctrl.submitted = false;
		ctrl.hideProjectDropDown = true;
		ctrl.hideTeamDropDown = true;
		ctrl.hideEstimateMetricDropDown = true;
		ctrl.hideSprintTypeDropDown = true;
        ctrl.hideListTypeDropDown = true;
		ctrl.evaluateTypeSelection = evaluateTypeSelection;

		// public variables
		ctrl.featureType = ctrl.featureTypeOption;
		ctrl.collectorItemId = null;
		ctrl.collectors = [];

		ctrl.projects = [];
		ctrl.selectedProject = null;
		ctrl.projectId = widgetConfig.options.projectId;
		ctrl.projectName = widgetConfig.options.projectName;

		ctrl.teams = [];
		ctrl.selectedTeam = null;
		ctrl.teamId = widgetConfig.options.teamId;
		ctrl.teamName = widgetConfig.options.teamName;

		ctrl.submit = submitForm;
		ctrl.featureTypeOption = "";
		ctrl.featureTypeOptions = [];
		ctrl.estimateMetricType = "";
		ctrl.estimateMetrics = [{type: "hours", value: "Hours"}, {type: "storypoints", value: "Story Points" }, {type: "count", value: "Issue Count" }];
		ctrl.sprintType = "";
		ctrl.sprintTypes = [{type: "scrum", value: "Scrum"}, {type: "kanban", value: "Kanban"}, {type: "scrumkanban", value:"Both"}];
		ctrl.listType = "";
		ctrl.listTypes = [{type: "epics", value: "Epics"}, {type: "issues", value: "Issues"}];

		// Request collectors
		collectorData.collectorsByType('AgileTool').then(
				processCollectorsResponse);

		initEstimateMetricType(widgetConfig);
		initSprintType(widgetConfig);
		initListType(widgetConfig);

		function processProjectsResponse(data) {
			var projects = [];
			var featureProjectId = widgetConfig.options.projectId;

			if (!ctrl.submitted && (ctrl.collectorId.value !== widgetConfig.options.featureTool) ) {
				featureProjectId = 'Any';
			}

			projects = getProjects(data, featureProjectId);

			ctrl.projectsDropdownPlaceholder = 'Select a Project';
			ctrl.projectsDropdownDisabled = false;

			function getProjects(data, currentProjectId) {
			    var anyItem = {
                    projectId : 'Any',
                    projectName : 'Any'
                };
                projects.push(anyItem);
                if (currentProjectId !== null && currentProjectId === 'Any') {
                    ctrl.selectedProjectIndex = 0;
                }
				for ( var x = 0; x < data.length; x++) {
					var obj = data[x];
					var item = {
						projectId : obj.pId,
						projectName : obj.name
					};

					projects.push(item);

					if (currentProjectId !== null && item.projectId === currentProjectId) {
						ctrl.selectedProjectIndex = x+1;
					}
				}

				ctrl.projects = projects;

				if ((ctrl.selectedProjectIndex === undefined) || (ctrl.selectedProjectIndex === null)) {
					ctrl.selectedProject = '';
				} else {
				    // TODO: check what valid is used for
					ctrl.valid = true;
					ctrl.selectedProject = ctrl.projects[ctrl.selectedProjectIndex];
				}
			}
		}


		function processTeamsResponse(data) {
			var teams = [];
			var featureTeamId = widgetConfig.options.teamId;

			if (!ctrl.submitted && (ctrl.collectorId.value !== widgetConfig.options.featureTool) ) {
				featureTeamId = 'Any';
			}

			teams = getTeams(data, featureTeamId);

			ctrl.teamsDropdownPlaceholder = 'Select a team';
			ctrl.teamsDropdownDisabled = false;

			function getTeams(data, currentTeamId) {
				var anyItem = {
					teamId : 'Any',
					teamName : 'Any'
				};
				teams.push(anyItem);
				if (currentTeamId !== null && currentTeamId === 'Any') {
					ctrl.selectedTeamIndex = 0;
				}
				for ( var x = 0; x < data.length; x++) {
					var obj = data[x];
					var item = {
						teamId : obj.teamId,
						teamName : obj.name
					};

					teams.push(item);

					if (currentTeamId !== null && item.teamId === currentTeamId) {
						ctrl.selectedTeamIndex = x+1;
					}
				}

				ctrl.teams = teams;

				if ((ctrl.selectedTeamIndex === undefined) || (ctrl.selectedTeamIndex === null)) {
					ctrl.selectedTeam = '';
				} else {
					// TODO: check what valid is used for
					ctrl.valid = true;
					ctrl.selectedTeam = ctrl.teams[ctrl.selectedTeamIndex];
				}
			}
		}

		function processCollectorsResponse(data) {
			ctrl.collectors = data;
			var featureCollector = modalData.dashboard.application.components[0].collectorItems.AgileTool;
			var featureCollectorId = featureCollector ? featureCollector[0].collectorId
					: null;

			getCollectors(data, featureCollectorId);

			function getCollectors(data, currentCollectorId) {
				for ( var x = 0; x < data.length; x++) {
					var obj = data[x];
					var item = {
						id : obj.id,
						value : obj.name,
					};

					ctrl.featureTypeOptions.push(item);

					if (currentCollectorId !== null && item.id === currentCollectorId) {
						ctrl.selectedTypeIndex = x;
					}
				}

				ctrl.typeDropdownPlaceholder = 'Select feature data source';
				ctrl.typeDropdownDisabled = false;

				if ((ctrl.selectedTypeIndex === undefined) || (ctrl.selectedTypeIndex === null)) {
					ctrl.collectorId = '';
					ctrl.hideProjectDropDown = true;
					ctrl.hideTeamDropDown = true;
					ctrl.hideSprintTypeDropDown = true;
					ctrl.hideListTypeDropDown = true;
				} else {
					ctrl.valid = true;
					ctrl.collectorId = ctrl.featureTypeOptions[ctrl.selectedTypeIndex];
					if (ctrl.collectorId.value === 'Jira') {
	                    ctrl.hideEstimateMetricDropDown = false;
	                } else {
	                    ctrl.hideEstimateMetricDropDown = true;
	                }
					ctrl.hideProjectDropDown = false;
					ctrl.hideTeamDropDown = false;
					ctrl.hideSprintTypeDropDown = false;
					ctrl.hideListTypeDropDown = false;

					// Request projects
					featureData.projectsByCollectorId(ctrl.collectorId.id).then(
						processProjectsResponse);

					// Request teams
					featureData.teamsByCollectorId(ctrl.collectorId.id).then(
						processTeamsResponse);
				}
			}
		}

		function initEstimateMetricType(widgetConfig) {
			if (widgetConfig.options.estimateMetricType != undefined && widgetConfig.options.estimateMetricType != null) {
				ctrl.estimateMetricType = widgetConfig.options.estimateMetricType;
			} else {
				ctrl.estimateMetricType = 'storypoints';
			}
		}

		function initSprintType(widgetConfig) {
			if (widgetConfig && widgetConfig.options && widgetConfig.options.sprintType) {
				ctrl.sprintType = widgetConfig.options.sprintType;
			} else {
				ctrl.sprintType = 'kanban';
			}
		}

		function initListType(widgetConfig) {
            if (widgetConfig && widgetConfig.options && widgetConfig.options.listType) {
                ctrl.listType = widgetConfig.options.listType;
            } else {
                ctrl.listType = 'epics';
            }
        }

		function evaluateTypeSelection() {
			if (ctrl.collectorId == null || ctrl.collectorId === "") {
				ctrl.hideProjectDropDown = true;
				ctrl.hideTeamDropDown = true;
				ctrl.hideEstimateMetricDropDown = true;
				ctrl.hideSprintTypeDropDown = true;
				ctrl.hideListTypeDropDown = true;
			} else {
				if (ctrl.collectorId.value === 'Jira') {
					ctrl.hideEstimateMetricDropDown = false;
				} else {
					ctrl.hideEstimateMetricDropDown = true;
				}
				ctrl.hideProjectDropDown = false;
				ctrl.hideTeamDropDown = false;
				ctrl.hideSprintTypeDropDown = false;
				ctrl.hideListTypeDropDown = false;
			}

			// Request projects
			featureData.projectsByCollectorId(ctrl.collectorId.id).then(
				processProjectsResponse);

			// Request teams
			featureData.teamsByCollectorId(ctrl.collectorId.id).then(
				processTeamsResponse);
		}

		function submitForm(valid) {
			ctrl.submitted = true;
			if (valid && ctrl.collectors.length) {

				createCollectorItem().then(processCollectorItemResponse);

			}
		}

		function createCollectorItem() {
			var item = {};
			var collectorId;

			if (ctrl.collectorId.value === 'Jira') {
				collectorId = _.findWhere(ctrl.collectors, {name: 'Jira'}).id
			} else if (ctrl.collectorId.value === 'VersionOne') {
				collectorId = _.findWhere(ctrl.collectors, {name: 'VersionOne'}).id
			}

			item = {
				collectorId: collectorId,
				options: {
					featureTool: ctrl.collectorId.value,
					teamName : ctrl.selectedTeam.teamName,
					teamId : ctrl.selectedTeam.teamId,
					projectName : ctrl.selectedProject.projectName,
					projectId : ctrl.selectedProject.projectId
				}
			};
			return collectorData.createCollectorItem(item);
		}

		function processCollectorItemResponse(response) {
			var postObj = {
				name : 'feature',
				options : {
					id : widgetConfig.options.id,
					featureTool: ctrl.collectorId.value,
					teamName : ctrl.selectedTeam.teamName,
					teamId : ctrl.selectedTeam.teamId,
					projectName : ctrl.selectedProject.projectName,
					projectId : ctrl.selectedProject.projectId,
					showStatus : { // starting configuration for what is currently showing. Needs to be mutually exclusive!
						kanban: "kanban" === ctrl.sprintType || "scrumkanban" === ctrl.sprintType,
						scrum: "scrum" === ctrl.sprintType
					},
					estimateMetricType : ctrl.estimateMetricType,
					sprintType: ctrl.sprintType,
					listType: ctrl.listType
				},
				componentId : modalData.dashboard.application.components[0].id,
				collectorItemId : response.data.id
			};

			// pass this new config to the modal closing so it's saved
			$uibModalInstance.close(postObj);
		}
	}
})();
