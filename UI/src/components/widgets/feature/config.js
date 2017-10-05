(function() {
	'use strict';

	angular.module(HygieiaConfig.module).controller('featureConfigController',
		featureConfigController);

	featureConfigController.$inject = [ 'modalData', '$uibModalInstance',
		'collectorData', 'featureData'];

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
		ctrl.projectId = widgetConfig.options.projectId;
		ctrl.projectName = widgetConfig.options.projectName;
		ctrl.teams = [];
		ctrl.teamId = widgetConfig.options.teamId;
		ctrl.teamName = widgetConfig.options.teamName;
		ctrl.featureTypeOption = "";
		ctrl.featureTypeOptions = [];
		ctrl.estimateMetricType = "";
		ctrl.estimateMetrics = [{type: "hours", value: "Hours"}, {type: "storypoints", value: "Story Points" }, {type: "count", value: "Issue Count" }];
		ctrl.sprintType = "";
		ctrl.sprintTypes = [{type: "scrum", value: "Scrum"}, {type: "kanban", value: "Kanban"}, {type: "scrumkanban", value:"Both"}];
		ctrl.listType = "";
		ctrl.listTypes = [{type: "epics", value: "Epics"}, {type: "issues", value: "Issues"}];
		ctrl.selectedProject = null;
		ctrl.selectedTeam = null;

		ctrl.submit = submitForm;
		ctrl.getProjectNames = getProjectNames;
		ctrl.getTeamNames = getTeamNames;
		ctrl.onSelectProject = onSelectProject;
		ctrl.onSelectTeam = onSelectTeam;

		// Request collectors
		collectorData.collectorsByType('AgileTool').then(
			processCollectorsResponse);
		// initialize inputs
		initEstimateMetricType(widgetConfig);
		initSprintType(widgetConfig);
		initListType(widgetConfig);
		initProjectName(widgetConfig);
		initTeamName(widgetConfig);
		initSelectedProjectAndTeam(widgetConfig);


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
				}
			}
		}


		function initProjectName(widgetConfig) {

			if (widgetConfig.options.projectName != undefined && widgetConfig.options.projectName != null) {
				ctrl.projectName = widgetConfig.options.projectName;
			}
		}

		function initTeamName(widgetConfig) {
			if (widgetConfig.options.teamName != undefined && widgetConfig.options.teamName != null) {
				ctrl.teamName = widgetConfig.options.teamName;
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

		function initSelectedProjectAndTeam(widgetConfig){
			if(widgetConfig && widgetConfig.options){
				ctrl.selectedProjectObject={
					name: widgetConfig.options.projectName,
					pId: widgetConfig.options.projectName==='Any'?'Any':widgetConfig.options.projectId
				}
				ctrl.selectedTeamObject ={
					name: widgetConfig.options.teamName,
					teamId:widgetConfig.options.teamName==='Any'?'Any':widgetConfig.options.teamId
				}
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

		}

		function onSelectProject(item,form){
			ctrl.selectedProjectObject  = item;
			setValidityForProjectAndTeam(form);
		}

		function onSelectTeam(item,form){
			ctrl.selectedTeamObject = item;
			setValidityForProjectAndTeam(form);
		}

		function setValidityForProjectAndTeam(form){
			if(ctrl.projectName ==="Any" && ctrl.teamName==="Any"){
				form.projectName.$setValidity('anyError',false);
				form.teamName.$setValidity('teamError',false);
				return;
			}else {
				form.projectName.$setValidity('anyError',true);
				form.teamName.$setValidity('teamError',true);
			}
		}

		function getProjectNames(filter) {
			return featureData.projectsByCollectorIdPaginated(ctrl.collectorId.id,{"search": filter, "size": 20, "sort": "description", "page": 0}).then(function (response) {
				if(!angular.isUndefined(filter)&& filter.match(/any/i)){
					var defaultValue={name:'Any',value:'Any',pId:'Any',teamId:'Any'}
					response.push(defaultValue);
				}
				return response;
			});
		}

		function getTeamNames(filter) {
			return featureData.teamsByCollectorIdPaginated(ctrl.collectorId.id,{"search": filter, "size": 20, "sort": "description", "page": 0}).then(function (response) {
				if(!angular.isUndefined(filter) && filter.match(/any/i)){
					var defaultValue={name:'Any',value:'Any',pId:'Any',teamId:'Any'}
					response.push(defaultValue);
				}
				return response;
			});
		}


		function submitForm(valid,form) {
			ctrl.submitted = true;
			form.projectName.$setValidity('anyError',true);
			form.projectName.$setValidity('teamError',true);
			setValidityForProjectAndTeam(form);
			if(form.$valid && ctrl.collectors.length){
				createCollectorItem().then(processCollectorItemResponse);
			}
		}

		function createCollectorItem() {
			var item = {};
			var collectorId;

			if (ctrl.collectorId.value === 'Jira') {
				collectorId = _.find(ctrl.collectors, {name: 'Jira'}).id
				item = createItemFromSelect(collectorId)
			} else if (ctrl.collectorId.value === 'VersionOne') {
				collectorId = _.find(ctrl.collectors, {name: 'VersionOne'}).id
				item = createItemFromSelect(collectorId)
			} else if (ctrl.collectorId.value ==='GitlabFeature') {
				collectorId = _.find(ctrl.collectors, {name: 'GitlabFeature'}).id
				item = {
					collectorId: collectorId,
					options: {
						featureTool: ctrl.collectorId.value,
						teamName : ctrl.teamId,
						teamId : ctrl.teamId,
						projectName : ctrl.projectId ? ctrl.projectId : "",
						projectId :ctrl.projectId ? ctrl.projectId : ""
					}
			}


			};
			return collectorData.createCollectorItem(item);
		}

		function createItemFromSelect(collectorId) {
			return {
				collectorId: collectorId,
				options: {
					featureTool: ctrl.collectorId.value,
					teamName : ctrl.selectedTeamObject.name,
					teamId : ctrl.selectedTeamObject.teamId,
					projectName : ctrl.selectedProjectObject.name,
					projectId :ctrl.selectedProjectObject.pId
				}
			}
		}

		function processCollectorItemResponse(response) {
			var postObj = {
				name : 'feature',
				options : {
					id : widgetConfig.options.id,
					featureTool: ctrl.collectorId.value,
					teamName : response.data.options.teamName,
					teamId : response.data.options.teamId,
					projectName : response.data.options.projectName,
					projectId : response.data.options.projectId,
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
