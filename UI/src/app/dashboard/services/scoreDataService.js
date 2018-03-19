/**
 * Service to handle all score data operations
*/
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .service('scoreDataService', scoreDataService);

    scoreDataService.$inject = [];
    function scoreDataService() {

        var _score = {};

        this.addDashboardScore = function (score) {
            if (!score) {
                return;
            }
            _score[score.scoreTypeId] = score;
        }

        this.getScoreByDashboardWidget = function (scoreTypeId, widgetId) {
            var score = _score[scoreTypeId];
            if (!score) {
                return null;
            }
            return _.find(score.componentMetrics, {refId: widgetId});
        }
    }
})();
