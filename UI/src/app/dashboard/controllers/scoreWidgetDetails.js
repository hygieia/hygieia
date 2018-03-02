/**
 * Detail controller for the score widget
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('ScoreWidgetDetailsController', ScoreWidgetDetailsController);

    ScoreWidgetDetailsController.$inject = ['$uibModalInstance', 'scoreWidget'];
    function ScoreWidgetDetailsController($uibModalInstance, scoreWidget) {
        var ctrl = this;
        ctrl.scoreWidget = scoreWidget;
        ctrl.scoreWidgetMetrics = [];
        ctrl.getIconClass = getIconClass;
        ctrl.close = close;
        ctrl.closeAlert = closeAlert;

        ctrl.alert = null;

        //Constants
        var STATE_COMPLETE = "complete";
        var STATE_NOT_PROCESSED = "not_processed";
        var STATE_CRITERIA_FAILED = "criteria_failed";
        var STATE_CRITERIA_PASSED = "criteria_passed";

        activate();

        function activate() {
            var typeDashboard = false;
            if (scoreWidget.scoreWidgetMetrics) {
              ctrl.scoreWidgetMetrics = scoreWidget.scoreWidgetMetrics;
              typeDashboard = true;
            } else {
              ctrl.scoreWidgetMetrics = scoreWidget.children;
            }
            setAlert(scoreWidget);
            updateStateProps(scoreWidget);

            _.forEach(ctrl.scoreWidgetMetrics, function (scoreWidgetMetric) {
                scoreWidgetMetric.percent = scoreWidgetMetric.weight + '%';
                updateStateProps(scoreWidgetMetric);
                if (scoreWidgetMetric.propagate && (
                        (typeDashboard && scoreWidgetMetric.propagate === "dashboard") ||
                        (!typeDashboard && scoreWidgetMetric.propagate === "widget")
                    )) {
                    scoreWidgetMetric.propagateScore = true;
                    scoreWidgetMetric.propagateMessage = "Propagate score to " +  scoreWidgetMetric.propagate;
                }
            });

        }

        function updateStateProps(scoreWidget) {
            var state = scoreWidget.state;
            if (state) {
                scoreWidget.statusTxt = getState(state);
                scoreWidget.statusClass = getStateClass(state);
                scoreWidget.statusIcon = getStateIcon(state);
            }
        }

        function setAlert(scoreWidget) {
            var message = scoreWidget.message;
            var state = scoreWidget.state;
            var alertClass = '';
            if (message && state) {
                if (state === STATE_COMPLETE || state === STATE_CRITERIA_PASSED) {
                    alertClass = 'alert-success';
                } else if (state === STATE_CRITERIA_FAILED) {
                    alertClass = 'alert-danger';
                } else if (state === STATE_NOT_PROCESSED) {
                    alertClass = 'alert-warning';
                }
                ctrl.alert = {
                    alertClass : alertClass,
                    message : message
                };
            }
        }

        function close() {
            $uibModalInstance.dismiss('close');
        }

        function getState(state) {
            if (state === STATE_COMPLETE) {
                return 'Processed';
            } else if (state === STATE_CRITERIA_FAILED) {
                return 'Criteria Failed';
            } else if (state === STATE_CRITERIA_PASSED) {
                return 'Criteria Passed';
            } else if (state === STATE_NOT_PROCESSED) {
                return 'Not Processed';
            }
        }

        function getStateClass(state) {
            if (state === STATE_COMPLETE) {
                return 'processed';
            } else if (state === STATE_CRITERIA_FAILED) {
                return 'criteria-failed';
            } else if (state === STATE_CRITERIA_PASSED) {
                return 'processed';
            } else if (state === STATE_NOT_PROCESSED) {
                return 'not-processed';
            }
        }

        function getStateIcon(state) {
            if (state === STATE_COMPLETE) {
                return 'fa-check';
            } else if (state === STATE_CRITERIA_FAILED) {
                return 'fa-times';
            } else if (state === STATE_CRITERIA_PASSED) {
                return 'fa-check-circle ';
            } else if (state === STATE_NOT_PROCESSED) {
                return 'fa-ban';
            }
        }

        function getIconClass(scoreWidget) {
            return scoreWidget.statusClass + ' ' + scoreWidget.statusIcon;
        }

        function closeAlert() {
            ctrl.alert = null;
        }
    }
})();
