(function() {
	'use strict';

	angular.module('devops-dashboard').controller('featureViewController',
			featureViewController);

	featureViewController.$inject = [ '$scope', 'featureData' ];

	function featureViewController($scope, featureData) {
		/* jshint validthis:true */
		var ctrl = this;
		var today = new Date(_.now());
		var filterTeamId = $scope.widgetConfig.options.teamId;
		ctrl.teamName = $scope.widgetConfig.options.teamName;
		ctrl.sprintName = null;
		ctrl.daysTilEnd = null;
		ctrl.totalStoryPoints = null;
		ctrl.wipStoryPoints = null;
		ctrl.doneStoryPoints = null;
		ctrl.epicStoryPoints = null;

		// Public Evaluators
		ctrl.totalStoryPointEvaluator = totalStoryPointEvaluator;
		ctrl.wipStoryPointEvaluator = wipStoryPointEvaluator;
		ctrl.doneStoryPointEvaluator = doneStoryPointEvaluator;
		ctrl.setFeatureLimit = setFeatureLimit;

		/**
		 * Every controller must have a load method. It will be called every 60
		 * seconds and should be where any calls to the data factory are made.
		 * To have a last updated date show at the top of the widget it must
		 * return a promise and then resolve it passing the lastUpdated
		 * timestamp.
		 */
		ctrl.load = function() {
			featureData.total($scope.widgetConfig.componentId, filterTeamId)
					.then(processTotalResponse);
			featureData.wip($scope.widgetConfig.componentId, filterTeamId)
					.then(processWipResponse);
			featureData.done($scope.widgetConfig.componentId, filterTeamId)
					.then(processDoneResponse);
			featureData.featureWip($scope.widgetConfig.componentId,
					filterTeamId).then(processFeatureWipResponse);
			featureData.sprint($scope.widgetConfig.componentId, filterTeamId)
					.then(processSprintResponse);
		};

		/**
		 * Processor for total feature estimate totals
		 * 
		 * @param data
		 */
		function processTotalResponse(data) {
			ctrl.totalStoryPoints = data.result[0].sEstimate;
		}

		/**
		 * Processor for in progress feature estimate in-progress
		 * 
		 * @param data
		 */
		function processWipResponse(data) {
			ctrl.wipStoryPoints = data.result[0].sEstimate;
		}

		/**
		 * Processor for done feature estimate in-progress
		 * 
		 * @param data
		 */
		function processDoneResponse(data) {
			ctrl.doneStoryPoints = data.result[0].sEstimate;
		}

		/**
		 * Processor for super feature estimates in-progress. Also sets the
		 * feature expander value based on the size of the data result set.
		 * 
		 * @param data
		 */
		function processFeatureWipResponse(data) {
			var epicCollection = [];

			for ( var i = 0; i < data.result.length; i++) {
				epicCollection.push(data.result[i]);
			}

			if (data.result.length <= 4) {
				ctrl.showFeatureLimitButton = false;
			} else {
				ctrl.showFeatureLimitButton = true;
			}

			ctrl.epicStoryPoints = epicCollection.sort(compare).reverse();
		}

		/**
		 * Custom object comparison used exclusively by the
		 * processFeatureWipResponse method; returns the comparison results for
		 * an array sort function based on integer values of estimates.
		 * 
		 * @param a
		 *            Object containing sEstimate string value
		 * @param b
		 *            Object containing sEstimate string value
		 */
		function compare(a, b) {
			if (parseInt(a.sEstimate) < parseInt(b.sEstimate))
				return -1;
			if (parseInt(a.sEstimate) > parseInt(b.sEstimate))
				return 1;
			return 0;
		}

		/**
		 * Processor for sprint-based data
		 * 
		 * @param data
		 */
		function processSprintResponse(data) {
			/*
			 * Sprint Name
			 */
			if (data.result[0].sSprintID === undefined) {
				ctrl.sprintName = "[No Sprint Available]";
			} else {
				ctrl.sprintName = data.result[0].sSprintName;
			}

			/*
			 * Days Until Sprint Expires
			 */
			if (data.result[0].sSprintID === undefined) {
				ctrl.daysTilEnd = moment(today).dash();
				ctrl.daysTilEnd = "[N/A]";
			} else {
				var nativeSprintEndDate = new Date(
						data.result[0].sSprintEndDate);
				if (nativeSprintEndDate < today) {
					ctrl.daysTilEnd = "[Ended]";
				} else {
					var nativeDaysTilEnd = moment(nativeSprintEndDate)
							.fromNow();
					ctrl.daysTilEnd = nativeDaysTilEnd.substr(3);
				}
			}
		}

		/**
		 * Evaluates the total story points based on the current sprint and team
		 * configuration for wholesomeness
		 */
		function totalStoryPointEvaluator() {
			var totalSum = ctrl.totalStoryPoints;
			var wipSum = ctrl.wipStoryPoints;
			var completeSum = ctrl.doneStoryPoints;
			var diffDays = ctrl.daysTilEnd;
			var readyRatio = 0;

			/*
			 * Analytical Calculations for Validation
			 */
			readyRatio = ((totalSum - wipSum - completeSum) / (completeSum + 0.01)) * 100;

			/*
			 * Validation of Current Sprint Status
			 */
			if (readyRatio <= 33.3) {
				// Good
				if (diffDays > 5) {
					return "pass";
				} else if ((diffDays <= 5) && (diffDays > 3)) {
					return "pass";
				} else {
					return "warn";
				}
			} else if ((readyRatio > 33.3) && (readyRatio <= 66.6)) {
				// Warn
				if (diffDays > 5) {
					return "warn";
				} else if ((diffDays <= 5) && (diffDays > 3)) {
					return "warn";
				} else {
					return "fail";
				}
			} else {
				// Danger
				if (diffDays > 5) {
					return "warn";
				} else if ((diffDays <= 5) && (diffDays > 3)) {
					return "fail";
				} else {
					return "fail";
				}
			}

			return "pass";
		}

		/**
		 * Evaluates the in progress story points based on the current sprint
		 * and team configuration for wholesomeness
		 */
		function wipStoryPointEvaluator() {
			var totalSum = ctrl.totalStoryPoints;
			var wipSum = ctrl.wipStoryPoints;
			var completeSum = ctrl.doneStoryPoints;
			var diffDays = ctrl.daysTilEnd;
			var wipRatio = 0;

			/*
			 * Analytical Calculations for Validation
			 */
			wipRatio = (wipSum / ((totalSum - completeSum - wipSum) + 0.01)) * 100;

			/*
			 * Validation of Current Sprint Status
			 */
			if (wipRatio >= 100.0) {
				// Good
				if (diffDays > 5) {
					return "pass";
				} else if ((diffDays <= 5) && (diffDays > 3)) {
					return "pass";
				} else {
					return "warn";
				}
			} else {
				// Danger
				if (diffDays > 5) {
					return "warn";
				} else if ((diffDays <= 5) && (diffDays > 3)) {
					return "fail";
				} else {
					return "fail";
				}
			}

			return "pass";
		}

		/**
		 * Evaluates the done story points based on the current sprint and team
		 * configuration for wholesomeness
		 */
		function doneStoryPointEvaluator() {
			var totalSum = ctrl.totalStoryPoints;
			var wipSum = ctrl.wipStoryPoints;
			var completeSum = ctrl.doneStoryPoints;
			var diffDays = ctrl.daysTilEnd;
			var completeRatio = 0;

			/*
			 * Analytical Calculations for Validation
			 */
			completeRatio = (completeSum / (totalSum + 0.01)) * 100;

			/*
			 * Validation of Current Sprint Status
			 */
			if (completeRatio <= 33.3) {
				// Danger
				if (diffDays > 5) {
					return "warn";
				} else if ((diffDays <= 5) && (diffDays > 3)) {
					return "fail";
				} else {
					return "fail";
				}
			} else if ((completeRatio > 33.3) && (completeRatio <= 66.6)) {
				// Warn
				if (diffDays > 5) {
					return "warn";
				} else if ((diffDays <= 5) && (diffDays > 3)) {
					return "warn";
				} else {
					return "fail";
				}
			} else {
				// Good
				if (diffDays > 5) {
					return "pass";
				} else if ((diffDays <= 5) && (diffDays > 3)) {
					return "pass";
				} else {
					return "warn";
				}
			}

			return "pass";
		}

		/**
		 * This method is used to help expand and contract the ever-growing
		 * super feature section on the Feature Widget
		 */
		function setFeatureLimit() {
			var featureMinLimit = 4;
			var featureMaxLimit = 99;

			if (ctrl.featureLimit > featureMinLimit) {
				ctrl.featureLimit = featureMinLimit;
			} else {
				ctrl.featureLimit = featureMaxLimit;
			}
		}
	}
})();
