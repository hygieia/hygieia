/**
 * Detail controller for the score component
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('ScoreComponentDetailsController', ScoreComponentDetailsController);

    ScoreComponentDetailsController.$inject = ['$uibModalInstance', 'scoreComponent'];
    function ScoreComponentDetailsController($uibModalInstance, scoreComponent) {
        var ctrl = this;
        ctrl.scoreComponent = scoreComponent;
        ctrl.componentMetrics = [];
        ctrl.showDetails = true;
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
            if (!ctrl.scoreComponent) {
                ctrl.showDetails = false;
                return;
            }
            var typeDashboard = false;
            if (ctrl.scoreComponent.componentMetrics) {
              ctrl.componentMetrics = ctrl.scoreComponent.componentMetrics;
              typeDashboard = true;
            } else {
              ctrl.componentMetrics = ctrl.scoreComponent.children;
            }
            setAlert(ctrl.scoreComponent);
            updateStateProps(ctrl.scoreComponent);

            _.forEach(ctrl.componentMetrics, function (componentMetric) {
                componentMetric.percent = componentMetric.weight + '%';
                updateStateProps(componentMetric);
                if (componentMetric.propagate && (
                        (typeDashboard && componentMetric.propagate === "dashboard") ||
                        (!typeDashboard && componentMetric.propagate === "widget")
                    )) {
                    componentMetric.propagateScore = true;
                    componentMetric.propagateMessage = "Propagate score to " +  componentMetric.propagate;
                }
            });

        }

        function updateStateProps(scoreComponent) {
            var state = scoreComponent.state;
            if (state) {
                scoreComponent.statusTxt = getState(state);
                scoreComponent.statusClass = getStateClass(state);
                scoreComponent.statusIcon = getStateIcon(state);
            }
        }

        function setAlert(scoreComponent) {
            var message = scoreComponent.message;
            var state = scoreComponent.state;
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

        function getIconClass(scoreComponent) {
            return scoreComponent.statusClass + ' ' + scoreComponent.statusIcon;
        }

        function closeAlert() {
            ctrl.alert = null;
        }
    }
})();
