(function() {
	'use strict';

	angular.module(HygieiaConfig.module).controller('featureConfigController',
			featureConfigController);

	featureConfigController.$inject = [ 'modalData', '$modalInstance',
			'collectorData' ];

	function featureConfigController(modalData, $modalInstance, collectorData) {
		/* jshint validthis:true */
		var ctrl = this;
		var widgetConfig = modalData.widgetConfig;

		// public state change variables
		ctrl.toolsDropdownPlaceholder = 'Loading Teams / Projects ...';
		ctrl.toolsDropdownDisabled = true;
		ctrl.typeDropdownPlaceholder = 'Loading Feature Data Sources ...';
		ctrl.typeDropdownDisabled = true;
		ctrl.estimateMetricDropdownDisabled = false;
		ctrl.submitted = false;
		ctrl.hideScopeOwnerDropDown = true;
		ctrl.evaluateTypeSelection = evaluateTypeSelection;

		// public variables
		ctrl.teamId = widgetConfig.options.teamId;
		ctrl.teamName = widgetConfig.options.teamName;
		ctrl.featureType = ctrl.featureTypeOption;
		ctrl.collectorItemId = null;
		ctrl.collectors = [];
		ctrl.scopeOwners = [];
		ctrl.permanentScopeOwners = [];
		ctrl.submit = submitForm;
		ctrl.featureTypeOption = "";
		ctrl.featureTypeOptions = [];
		ctrl.estimateMetricType = "";
		ctrl.estimateMetrics = [{type: "hours", value: "Hours"}, {type: "storypoints", value: "Story Points" }];


		// Request collectors
		collectorData.collectorsByType('scopeowner').then(
				processCollectorsResponse);

		// Request collector items
		collectorData.itemsByType('scopeowner').then(
				processCollectorItemsResponse);
		
		initEstimateMetricType(widgetConfig);

		function processCollectorItemsResponse(data, currentCollectorItemId) {
			var scopeOwners = [];
			var featureCollector = modalData.dashboard.application.components[0].collectorItems.ScopeOwner;
			var featureCollectorId = featureCollector ? featureCollector[0].id
					: null;

			if (ctrl.collectorId !== "") {
				scopeOwners = getScopeOwners(data, featureCollectorId);
				evaluateTypeSelection();
			} else {
				getPermanentScopeOwners(data, featureCollectorId);
				evaluateTypeSelection();
			}

			ctrl.toolsDropdownPlaceholder = 'Select a scope owner';
			ctrl.toolsDropdownDisabled = false;

			function getPermanentScopeOwners(data, currentCollectorItemId) {
				for ( var x = 0; x < data.length; x++) {
					var obj = data[x];
					var item = {
						value : obj.id,
						teamId : obj.options.teamId,
						teamName : obj.collector.name + ' - ' + obj.description
					};

					ctrl.permanentScopeOwners.push(item);

					if (currentCollectorItemId !== null	&& item.value == currentCollectorItemId) {
						ctrl.selectedIndex = x;
					}
				}
			}

			function getScopeOwners(data, currentCollectorItemId) {
				for ( var x = 0; x < data.length; x++) {
					var obj = data[x];
					var item = {
						value : obj.id,
						teamId : obj.options.teamId,
						teamName : obj.collector.name + ' - ' + obj.description
					};

					scopeOwners.push(item);

					if (currentCollectorItemId !== null	&& item.value == currentCollectorItemId) {
						ctrl.selectedIndex = x;
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

		function processCollectorsResponse(data, currentCollectorId) {
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

					if (currentCollectorId !== null && item.id == currentCollectorId) {
						ctrl.selectedTypeIndex = x;
					}
				}

				ctrl.typeDropdownPlaceholder = 'Select feature data source';
				ctrl.typeDropdownDisabled = false;

				if ((ctrl.selectedTypeIndex === undefined) || (ctrl.selectedTypeIndex === null)) {
					ctrl.collectorId = '';
					ctrl.hideScopeOwnerDropDown = true;
				} else {
					ctrl.valid = true;
					ctrl.collectorId = ctrl.featureTypeOptions[ctrl.selectedTypeIndex];
					ctrl.hideScopeOwnerDropDown = false;
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

		function evaluateTypeSelection() {
			var tempTypeOptions = [];
			for ( var x = 0; x < ctrl.permanentScopeOwners.length; x++) {
				var sampleScopeOwner = ctrl.permanentScopeOwners[x].teamName
						.substr(0, ctrl.permanentScopeOwners[x].teamName
								.indexOf(' '));
				if (ctrl.collectorId != null && sampleScopeOwner === ctrl.collectorId.value) {
					// TODO: remove record from ctrl.scopeowner
					tempTypeOptions.push(ctrl.permanentScopeOwners[x]);
				}
			}
			ctrl.scopeOwners = tempTypeOptions;

			if (ctrl.collectorId == null || ctrl.collectorId === "") {
				ctrl.hideScopeOwnerDropDown = true;
				ctrl.hideEstimateMetricDropDown = true;
			} else {
				if (ctrl.collectorId.value === 'Jira') {
					ctrl.hideEstimateMetricDropDown = false;
				} else {
					ctrl.hideEstimateMetricDropDown = true;
				}
				ctrl.hideScopeOwnerDropDown = false;
			}
		}

		function submitForm(valid, collectorItemId, estimateMetricType) {
			ctrl.submitted = true;
			if (valid && ctrl.collectors.length) {
				processCollectorItemResponse(collectorItemId, estimateMetricType);
			}
		}

		function processCollectorItemResponse(collectorItemId, estimateMetricType) {
			var postObj = {
				name : 'feature',
				options : {
					id : widgetConfig.options.id,
					teamName : ctrl.collectorItemId.teamName,
					teamId : ctrl.collectorItemId.teamId,
					showStatus : {
				      kanban: true,
				      scrum: false
				    },
					intervalOff : 2,
					estimateMetricType : ctrl.estimateMetricType
				},
				componentId : modalData.dashboard.application.components[0].id,
				collectorItemId : ctrl.collectorItemId.value
			};
			// pass this new config to the modal closing so it's saved
			$modalInstance.close(postObj);
		}
	}
})();
