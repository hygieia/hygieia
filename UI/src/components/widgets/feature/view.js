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
    ctrl.wipStoryPoints = null;
    ctrl.doneStoryPoints = null;
    ctrl.epicStoryPoints = null;
    // Kanban
    ctrl.iterationsKanban = [];
    ctrl.totalStoryPointsKanban = null;
    ctrl.wipStoryPointsKanban = null;
    ctrl.doneStoryPointsKanban = null;
    ctrl.epicStoryPointsKanban = null;

    // Public Evaluators
    ctrl.setFeatureLimit = setFeatureLimit;
    ctrl.showStatus = $scope.widgetConfig.options.showStatus;
    ctrl.animateAgileView = animateAgileView;
    ctrl.intervalOff = $scope.widgetConfig.options.intervalOff;
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
        featureData.total($scope.widgetConfig.componentId, filterTeamId, estimateMetricType)
          .then(processTotalResponse),
        featureData.wip($scope.widgetConfig.componentId, filterTeamId, estimateMetricType)
          .then(processWipResponse),
        featureData.done($scope.widgetConfig.componentId, filterTeamId, estimateMetricType)
          .then(processDoneResponse),
        featureData.featureWip($scope.widgetConfig.componentId,
          filterTeamId, estimateMetricType).then(processFeatureWipResponse),
        featureData.sprint($scope.widgetConfig.componentId, filterTeamId)
          .then(processSprintResponse),

        // Kanban
        featureData.totalKanban($scope.widgetConfig.componentId, filterTeamId, estimateMetricType)
          .then(processTotalKanbanResponse),
        featureData.wipKanban($scope.widgetConfig.componentId, filterTeamId, estimateMetricType)
          .then(processWipKanbanResponse),
        featureData.featureWipKanban($scope.widgetConfig.componentId,
          filterTeamId, estimateMetricType).then(processFeatureWipKanbanResponse),
        featureData.sprintKanban($scope.widgetConfig.componentId, filterTeamId)
          .then(processSprintKanbanResponse)
      ]);

      deferred.then(function(){
        detectIterationChange();
      });
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
     * Processor for total feature estimate totals for kanban only
     *
     * @param data
     */
    function processTotalKanbanResponse(data) {
      ctrl.totalStoryPointsKanban = data.result[0].sEstimate;
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
     * Processor for in progress feature estimate in-progress for kanban only
     *
     * @param data
     */
    function processWipKanbanResponse(data) {
      ctrl.wipStoryPointsKanban = data.result[0].sEstimate;
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
    function processSprintResponse(data) {
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
      ctrl.intervalOff = $scope.widgetConfig.options.intervalOff;


      for (var i = 0; i < data.result.length; i++) {
        if (data.result[i].sSprintID === undefined) {
          sprintID = "[No Sprint Available]"
          sprintName = "[No Sprint Available]";
        } else {
          sprintID = data.result[i].sSprintID;
          sprintName = data.result[i].sSprintName;
        }

        /*
         * Days Until Sprint Expires
         */
        if (data.result[i].sSprintID === undefined) {
          daysTilEnd = moment(today).dash();
          daysTilEnd = "[N/A]";
        } else if (data.result[i].sSprintID === "KANBAN") {
          daysTilEnd = "[Unlimited]";
        } else {
          var nativeSprintEndDate = new Date(
            data.result[i].sSprintEndDate);
          if (nativeSprintEndDate < today) {
            daysTilEnd = "[Ended]";
          } else {
            var nativeDaysTilEnd = moment(nativeSprintEndDate)
              .fromNow();
            daysTilEnd = nativeDaysTilEnd.substr(3);
          }
        }

        // Fill one iteration object at a time, starting with the first
        if (ctrl.iterations.length <= 0) {
          iteration = {
            id: sprintID,
            name: sprintName,
            tilEnd: daysTilEnd
          };
          ctrl.iterations.push(iteration);
        }

        // Add iterations only if there are no duplicates
        if (isInArray(sprintID, ctrl.iterations) === false) {
          iteration = {
            id: sprintID,
            name: sprintName,
            tilEnd: daysTilEnd
          };
          ctrl.iterations.push(iteration);
        }

        // Clean-up
        sprintID = null;
        sprintName = null;
        daysTilEnd = null;
        iteration = null;

        /*
         * Checks iterations array for existing elements
         */
        function isInArray(timebox, iterations) {
          var dupe = false;

          iterations.forEach(function(timebox) {
            if (timebox.id === sprintID) {
              dupe = true;
            }
          });

          return dupe;
        }
      }

      // Check if iteration switching is needed
      if (ctrl.iterations.length < 1) {
        ctrl.showStatus.scrum = false;
        ctrl.intervalOff --;
      }
    }

    /**
     * Processor for sprint-based data for kanban
     *
     * @param data
     */
    function processSprintKanbanResponse(data) {
      /*
       * Sprint Name
       */
      var sprintID = null;
      var sprintName = null;
      var daysTilEnd = null;
      var iteration = null;
      var dupes = true;

      for (var i = 0; i < data.result.length; i++) {
        if (data.result[i].sSprintID === undefined) {
          sprintID = "[No Sprint Available]"
          sprintName = "[No Sprint Available]";
        } else {
          sprintID = data.result[i].sSprintID;
          sprintName = data.result[i].sSprintName;
        }

        /*
         * Days Until Sprint Expires
         */
        if (data.result[i].sSprintID === undefined) {
          daysTilEnd = moment(today).dash();
          daysTilEnd = "[N/A]";
        } else if (data.result[i].sSprintID === "KANBAN") {
          daysTilEnd = "[Unlimited]";
        } else {
          var nativeSprintEndDate = new Date(
            data.result[i].sSprintEndDate);
          if (nativeSprintEndDate < today) {
            daysTilEnd = "[Ended]";
          } else {
            var nativeDaysTilEnd = moment(nativeSprintEndDate)
              .fromNow();
            daysTilEnd = nativeDaysTilEnd.substr(3);
          }
        }

        // Fill one iteration object at a time, starting with the first
        if (ctrl.iterationsKanban.length <= 0) {
          iteration = {
            id: sprintID,
            name: sprintName,
            tilEnd: daysTilEnd
          };
          ctrl.iterationsKanban.push(iteration);
        }

        // Add iterations only if there are no duplicates
        if (isInArray(sprintID, ctrl.iterationsKanban) === false) {
          iteration = {
            id: sprintID,
            name: sprintName,
            tilEnd: daysTilEnd
          };
          ctrl.iterationsKanban.push(iteration);
        }

        // Clean-up
        sprintID = null;
        sprintName = null;
        daysTilEnd = null;
        iteration = null;

        /*
         * Checks iterations array for existing elements
         */
        function isInArray(timebox, iterations) {
          var dupe = false;

          iterations.forEach(function(timebox) {
            if (timebox.id === sprintID) {
              dupe = true;
            }
          });

          return dupe;
        }
      }

      // Check if iteration switching is needed
      if (ctrl.iterationsKanban.length < 1) {
        ctrl.showStatus.kanban = false;
        ctrl.intervalOff --;
      }
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
        if (ctrl.intervalOff === 2) {
          animateAgileView(true);
        } else if (ctrl.intervalOff === 1) {
          animateAgileView(false);
        }
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
    function animateAgileView(multipleDetects) {
      switch (multipleDetects) {
        case true:
          // Swap Kanban
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
          break;
        case false:
          // Use case for clean up and one time loads
          if (ctrl.iterationsKanban.length >= 1) {
            ctrl.showStatus.kanban = true;
            ctrl.showStatus.scrum = false;
          } else if (ctrl.iterations.length >= 1) {
            ctrl.showStatus.scrum = true;
            ctrl.showStatus.kanban = false;
          }
          break;
        default:
          ctrl.showStatus.scrum = false;
          ctrl.showStatus.kanban = false;
          console.log("This shouldn't happen!  Please raise an issue on GitHub.");
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
