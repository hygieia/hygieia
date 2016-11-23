(function() {
	'use strict';

	angular.module(HygieiaConfig.module).controller('featureConfigController',
			featureConfigController);

	featureConfigController.$inject = [ 'modalData', '$modalInstance',
			'collectorData', 'featureData' ];

	function featureConfigController(modalData, $modalInstance, collectorData, featureData) {
		/* jshint validthis:true */
		var ctrl = this;
		var widgetConfig = modalData.widgetConfig;

		// public state change variables
		ctrl.toolsDropdownPlaceholder = 'Loading Teams ...';
		ctrl.toolsDropdownDisabled = true;
		ctrl.projectsDropdownPlaceholder = 'Loading Projects ...';
		ctrl.projectsDropdownDisabled = true;
		ctrl.typeDropdownPlaceholder = 'Loading Feature Data Sources ...';
		ctrl.typeDropdownDisabled = true;
		ctrl.estimateMetricDropdownDisabled = false;
		ctrl.submitted = false;
		ctrl.hideScopeOwnerDropDown = true;
		ctrl.hideProjectDropDown = true;
		ctrl.hideEstimateMetricDropDown = true;
		ctrl.hideSprintTypeDropDown = true;
        ctrl.hideListTypeDropDown = true;
		ctrl.evaluateTypeSelection = evaluateTypeSelection;

		// public variables
		ctrl.teamId = widgetConfig.options.teamId;
		ctrl.teamName = widgetConfig.options.teamName;
		ctrl.featureType = ctrl.featureTypeOption;
		ctrl.collectorItemId = null;
		ctrl.collectors = [];
		ctrl.projects = [];
		ctrl.permanentProjects = [];
		ctrl.selectedProject = null;
		ctrl.projectId = widgetConfig.options.projectId;
		ctrl.projectName = widgetConfig.options.projectName;
		ctrl.scopeOwners = [];
		ctrl.permanentScopeOwners = [];
		ctrl.submit = submitForm;
		ctrl.featureTypeOption = "";
		ctrl.featureTypeOptions = [];
		ctrl.estimateMetricType = "";
		ctrl.estimateMetrics = [{type: "hours", value: "Hours"}, {type: "storypoints", value: "Story Points" }];
		ctrl.sprintType = "";
		ctrl.sprintTypes = [{type: "scrum", value: "Scrum"}, {type: "kanban", value: "Kanban"}, {type: "scrumkanban", value:"Both"}];
		ctrl.listType = "";
		ctrl.listTypes = [{type: "epics", value: "Epics"}, {type: "issues", value: "Issues"}];


		// Request collectors
		collectorData.collectorsByType('scopeowner').then(
				processCollectorsResponse);

		// Request collector items
		collectorData.itemsByType('scopeowner').then(
				processCollectorItemsResponse);
		
		// Request projects
		featureData.projects().then(
				processProjectsResponse);
		
		initEstimateMetricType(widgetConfig);
		initSprintType(widgetConfig);
		initListType(widgetConfig);

		function processProjectsResponse(data) {
			var projects = [];
			var featureProjectId = widgetConfig.options.projectId;
			
			if (ctrl.collectorId !== "") {
				projects = getProjects(data, featureProjectId);
				evaluateTypeSelection();
			} else {
				getPermanentProjects(data, featureProjectId);
				evaluateTypeSelection();
			}

			ctrl.projectsDropdownPlaceholder = 'Select a scope';
			ctrl.projectsDropdownDisabled = false;

			function getPermanentProjects(data, currentProjectId) {
			    var anyItem = {
                    projectId : 'Any',
                    projectName : 'Any'
                };
			    ctrl.permanentProjects.push(anyItem);
                if (currentProjectId !== null && currentProjectId === 'Any') {
                    ctrl.selectedProjectIndex = 0;
                }
				for ( var x = 0; x < data.length; x++) {
					var obj = data[x];
					var item = {
                        projectId : obj.pId,
                        projectName : obj.collector.name + ' - ' + obj.name
                    };

					ctrl.permanentProjects.push(item);

					if (currentProjectId !== null && item.projectId === currentProjectId) {
                        ctrl.selectedProjectIndex = x+1;
                    }
				}
			}

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
						projectName : obj.collector.name + ' - ' + obj.name
					};

					projects.push(item);

					if (currentProjectId !== null && item.projectId === currentProjectId) {
						ctrl.selectedProjectIndex = x+1;
					}
				}

				ctrl.projects = projects;
				ctrl.permanentProjects = projects;

				if ((ctrl.selectedProjectIndex === undefined) || (ctrl.selectedProjectIndex === null)) {
					ctrl.selectedProject = '';
				} else {
				    // TODO: check what valid is used for
					ctrl.valid = true;
					ctrl.selectedProject = ctrl.projects[ctrl.selectedProjectIndex];
				}
			}
		}
		
		function processCollectorItemsResponse(data) {
			var scopeOwners = [];
			var featureCollector = modalData.dashboard.application.components[0].collectorItems.ScopeOwner;
			var featureTeamId = widgetConfig.options.teamId;

			if (ctrl.collectorId !== "") {
				scopeOwners = getScopeOwners(data, featureTeamId);
				evaluateTypeSelection();
			} else {
				getPermanentScopeOwners(data, featureTeamId);
				evaluateTypeSelection();
			}

			ctrl.toolsDropdownPlaceholder = 'Select a scope owner';
			ctrl.toolsDropdownDisabled = false;

			function getPermanentScopeOwners(data, currentTeamId) {
			    var anyItem = {
			        value : 'Any',
                    teamId : 'Any',
                    teamName : 'Any'
                };
                ctrl.permanentScopeOwners.push(anyItem);
                if (currentTeamId !== null && currentTeamId === 'Any') {
                    ctrl.selectedIndex = 0;
                }
				for ( var x = 0; x < data.length; x++) {
					var obj = data[x];
					var item = {
						value : obj.id,
						teamId : obj.options.teamId,
						teamName : obj.collector.name + ' - ' + obj.description
					};

					ctrl.permanentScopeOwners.push(item);

					if (currentTeamId !== null	&& item.teamId === currentTeamId) {
						ctrl.selectedIndex = x+1;
					}
				}
			}

			function getScopeOwners(data, currentTeamId) {
			    var anyItem = {
                    value : 'Any',
                    teamId : 'Any',
                    teamName : 'Any'
                };
                scopeOwners.push(anyItem);
                if (currentTeamId !== null && currentTeamId === 'Any') {
                    ctrl.selectedIndex = 0;
                }
				for ( var x = 0; x < data.length; x++) {
					var obj = data[x];
					var item = {
						value : obj.id,
						teamId : obj.options.teamId,
						teamName : obj.collector.name + ' - ' + obj.description
					};

					scopeOwners.push(item);

					if (currentTeamId !== null	&& item.teamId === currentTeamId) {
						ctrl.selectedIndex = x+1;
					}
				}

				ctrl.scopeOwners = scopeOwners;
				ctrl.permanentScopeOwners = scopeOwners;

				if ((ctrl.selectedIndex === undefined) || (ctrl.selectedIndex === null)) {
					ctrl.collectorItemId = '';
				} else {
					ctrl.valid = true;
					ctrl.collectorItemId = ctrl.scopeOwners[ctrl.selectedIndex];
				}
			}
		}

		function processCollectorsResponse(data) {
			ctrl.collectors = data;
			var featureCollector = modalData.dashboard.application.components[0].collectorItems.ScopeOwner;
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
					ctrl.hideScopeOwnerDropDown = true;
					ctrl.hideProjectDropDown = true;
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
					ctrl.hideScopeOwnerDropDown = false;
					ctrl.hideProjectDropDown = false;
					ctrl.hideSprintTypeDropDown = false;
					ctrl.hideListTypeDropDown = false;
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
			var tempTypeOptions = [];
			for ( var x = 0; x < ctrl.permanentScopeOwners.length; x++) {
				var sampleScopeOwner = ctrl.permanentScopeOwners[x].teamName
						.substr(0, ctrl.permanentScopeOwners[x].teamName
								.indexOf(' '));
				if (ctrl.collectorId != null && (ctrl.permanentScopeOwners[x].teamName === 'Any'
				                                    || sampleScopeOwner === ctrl.collectorId.value)) {
					// TODO: remove record from ctrl.scopeowner
					tempTypeOptions.push(ctrl.permanentScopeOwners[x]);
				}
			}
			ctrl.scopeOwners = tempTypeOptions;
			
			var tempProjects = [];
			for ( var x = 0; x < ctrl.permanentProjects.length; x++) {
			    var sampleProject = ctrl.permanentProjects[x].projectName
                                        .substr(0, ctrl.permanentProjects[x].projectName
                                        .indexOf(' '));
                if (ctrl.collectorId != null && (ctrl.permanentProjects[x].projectName === 'Any' 
                                                    || sampleProject === ctrl.collectorId.value)) {
                    tempProjects.push(ctrl.permanentProjects[x]);
                }
            }
			ctrl.projects = tempProjects;

			if (ctrl.collectorId == null || ctrl.collectorId === "") {
				ctrl.hideScopeOwnerDropDown = true;
				ctrl.hideProjectDropDown = true;
				ctrl.hideEstimateMetricDropDown = true;
				ctrl.hideSprintTypeDropDown = true;
                ctrl.hideListTypeDropDown = true;
			} else {
				if (ctrl.collectorId.value === 'Jira') {
					ctrl.hideEstimateMetricDropDown = false;
				} else {
					ctrl.hideEstimateMetricDropDown = true;
				}
				ctrl.hideScopeOwnerDropDown = false;
				ctrl.hideProjectDropDown = false;
				ctrl.hideSprintTypeDropDown = false;
                ctrl.hideListTypeDropDown = false;
			}
		}

		function submitForm(valid, collectorItemId, estimateMetricType) {
			ctrl.submitted = true;
			if (valid && ctrl.collectors.length) {
				processCollectorItemResponse(collectorItemId, estimateMetricType);
			}
		}

		function processCollectorItemResponse(collectorItemId, estimateMetricType) {
			var postObj = null
			if (ctrl.collectorItemId.value === 'Any') {
    			postObj = {
    				name : 'feature',
    				options : {
    					id : widgetConfig.options.id,
    					teamName : ctrl.collectorItemId.teamName,
    					teamId : ctrl.collectorItemId.teamId,
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
    				componentId : modalData.dashboard.application.components[0].id
    			};
			} else {
			    postObj = {
                    name : 'feature',
                    options : {
                        id : widgetConfig.options.id,
                        teamName : ctrl.collectorItemId.teamName,
                        teamId : ctrl.collectorItemId.teamId,
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
                    collectorItemId : ctrl.collectorItemId.value
                };    
    		}
			
			// pass this new config to the modal closing so it's saved
			$modalInstance.close(postObj);
		}
	}
})();
