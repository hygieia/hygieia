(function() {
  'use strict';

  angular.module(HygieiaConfig.module).controller('featureViewController',
    featureViewController);

  featureViewController.$inject = ['$scope', '$q', '$interval', 'featureData'];

  function featureViewController($scope, $q, $interval, featureData) {
    /* jshint validthis:true */
    var ctrl = this;
    var today = new Date(_.now());
    var filterTeamId = $scope.widgetConfig.options.teamId;
    var estimateMetricType = $scope.widgetConfig.options.estimateMetricType;
    ctrl.teamName = $scope.widgetConfig.options.teamName;
    // Scrum
    ctrl.iterations = [];
    ctrl.totalStoryPoints = null;
    ctrl.openStoryPoints = null;
    ctrl.wipStoryPoints = null;
    ctrl.doneStoryPoints = null;
    ctrl.epicStoryPoints = null;
    // Kanban
    ctrl.iterationsKanban = [];
    ctrl.totalStoryPointsKanban = null;
    ctrl.openStoryPointsKanban = null;
    ctrl.wipStoryPointsKanban = null;
    ctrl.doneStoryPointsKanban = null;
    ctrl.epicStoryPointsKanban = null;

    // Public Evaluators
    ctrl.setFeatureLimit = setFeatureLimit;
    ctrl.showStatus = $scope.widgetConfig.options.showStatus;
    ctrl.animateAgileView = animateAgileView;
    ctrl.numberOfSprintTypes = $scope.widgetConfig.options.sprintType === "scrumkanban" ? 2 : 1;
    
    var timeoutPromise = null;
    ctrl.changeDetect = null;
    ctrl.pauseAgileView = pauseAgileView;
    ctrl.pausePlaySymbol = "||";

    /**
     * Every controller must have a load method. It will be called every 60
     * seconds and should be where any calls to the data factory are made.
     * To have a last updated date show at the top of the widget it must
     * return a promise and then resolve it passing the lastUpdated
     * timestamp.
     */
    ctrl.load = function() {
      var deferred = $q.all([
        // Scrum
        featureData.sprintMetrics($scope.widgetConfig.componentId, filterTeamId, estimateMetricType, "scrum").then(processSprintEstimateResponse),
        featureData.featureWip($scope.widgetConfig.componentId, filterTeamId, estimateMetricType, "scrum").then(processFeatureWipResponse),
        featureData.sprint($scope.widgetConfig.componentId, filterTeamId, "scrum")
          .then(function(data) { processSprintResponse(data, false) }),

        // Kanban
        featureData.sprintMetrics($scope.widgetConfig.componentId, filterTeamId, estimateMetricType, "kanban").then(processSprintEstimateKanbanResponse),
        featureData.featureWip($scope.widgetConfig.componentId, filterTeamId, estimateMetricType, "kanban").then(processFeatureWipKanbanResponse),
        featureData.sprint($scope.widgetConfig.componentId, filterTeamId, "kanban")
          .then(function(data) { processSprintResponse(data, true) })
      ]);

      deferred.then(function(){
        detectIterationChange();
      });
    };
    
    function processSprintEstimateResponse(data) {
        ctrl.totalStoryPoints = data.result.totalEstimate;
        ctrl.openStoryPoints = data.result.openEstimate;
        ctrl.wipStoryPoints = data.result.inProgressEstimate;
        ctrl.doneStoryPoints = data.result.completeEstimate;
    }
    
    function processSprintEstimateKanbanResponse(data) {
        ctrl.totalStoryPointsKanban = data.result.totalEstimate;
        ctrl.openStoryPointsKanban = data.result.openEstimate;
        ctrl.wipStoryPointsKanban = data.result.inProgressEstimate;
        ctrl.doneStoryPointsKanban = data.result.completeEstimate;
    }

    /**
     * Processor for super feature estimates in-progress. Also sets the
     * feature expander value based on the size of the data result set.
     *
     * @param data
     */
    function processFeatureWipResponse(data) {
      var epicCollection = [];

      for (var i = 0; i < data.result.length; i++) {
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
     * Processor for super feature estimates in-progress. Also sets the
     * feature expander value based on the size of the data result set
     * for kanban only.
     *
     * @param data
     */
    function processFeatureWipKanbanResponse(data) {
      var epicCollection = [];

      for (var i = 0; i < data.result.length; i++) {
        epicCollection.push(data.result[i]);
      }

      if (data.result.length <= 4) {
        ctrl.showFeatureLimitButton = false;
      } else {
        ctrl.showFeatureLimitButton = true;
      }

      ctrl.epicStoryPointsKanban = epicCollection.sort(compare).reverse();
    }

    /**
     * Processor for sprint-based data
     *
     * @param data
     */
    function processSprintResponse(data, isKanban) {
      /*
       * Sprint Name
       */
      var sprintID = null;
      var sprintName = null;
      var daysTilEnd = null;
      var iteration = null;
      var dupes = true;
      // Reset on every processing
      ctrl.showStatus = $scope.widgetConfig.options.showStatus;

      var iterations = isKanban? ctrl.iterationsKanban : ctrl.iterations;

      for (var i = 0; i < data.result.length; i++) {
        if (data.result[i].sSprintID === undefined) {
          sprintID = "[No Sprint Available]";
          sprintName = "[No Sprint Available]";
        } else {
          sprintID = data.result[i].sSprintID;
          sprintName = data.result[i].sSprintName;
        }
        
        if (isKanban && (sprintID == null || sprintID === "" )) {
        	sprintID = "KANBAN"
        	sprintName = "KANBAN"
        }

        /*
         * Days Until Sprint Expires
         */
        if (data.result[i].sSprintID === undefined) {
          daysTilEnd = "[N/A]";
        } else if (isKanban) {
          daysTilEnd = "[Unlimited]";
        } else {
          var nativeSprintEndDate = new Date(data.result[i].sSprintEndDate);
          if (nativeSprintEndDate < today) {
            daysTilEnd = "[Ended]";
          } else {
            var nativeDaysTilEnd = moment(nativeSprintEndDate).fromNow();
            daysTilEnd = nativeDaysTilEnd.substr(3);
          }
        }
        
        // Add iterations only if there are no duplicates
        if (isInArray(sprintID, iterations) === false) {
          iteration = {
            id: sprintID,
            name: sprintName,
            tilEnd: daysTilEnd
          };
          iterations.push(iteration);
        }
        
        // Clean-up
        sprintID = null;
        sprintName = null;
        daysTilEnd = null;
        iteration = null;
      }
    }
    
    /*
     * Checks iterations array for existing elements
     */
    function isInArray(sprintID, iterations) {
      var dupe = false;

      iterations.forEach(function(timebox) {
        if (timebox.id === sprintID) {
          dupe = true;
        }
      });

      return dupe;
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

    /**
     * Changes timeout boolean based on agile iterations available,
     * turning off the agile view switching if only one or none are
     * available
     */
    ctrl.startTimeout = function() {
      ctrl.stopTimeout();

      timeoutPromise = $interval(function() {
          animateAgileView(false);
      }, 7000);
    }

    /**
     * Stops the current agile iteration cycler promise
     */
    ctrl.stopTimeout = function() {
      $interval.cancel(timeoutPromise);
    };

    /**
     * Starts timeout cycle function by default
     */
    ctrl.startTimeout();

    /**
     * Triggered by the resolution of the data factory promises, iterations
     * types are detected from their resolutions and then initialized based
     * on data results.  This is a one time action per promise resolution.
     */
    function detectIterationChange () {
      animateAgileView(false);
    }

    /**
     * Animates agile view switching
     */
	function animateAgileView(resetTimer) {
		if (ctrl.numberOfSprintTypes > 1) {
			if (ctrl.showStatus.kanban === false) {
				ctrl.showStatus.kanban = true;
			} else if (ctrl.showStatus.kanban === true) {
				ctrl.showStatus.kanban = false;
			}

			// Swap Scrum
			if (ctrl.showStatus.scrum === false) {
				ctrl.showStatus.scrum = true;
			} else if (ctrl.showStatus.scrum === true) {
				ctrl.showStatus.scrum = false;
			}
		}
		
		if (resetTimer && timeoutPromise.$$state.value != "canceled") {
			ctrl.stopTimeout();
			ctrl.startTimeout();
		}
	}

    /**
	 * Pauses agile view switching via manual button from user interaction
	 */
    function pauseAgileView() {
      if (timeoutPromise.$$state.value === "canceled") {
        ctrl.pausePlaySymbol = "||";
        ctrl.startTimeout();
      } else {
        ctrl.pausePlaySymbol = ">";
        ctrl.stopTimeout();
      }
    };
  }
})();

