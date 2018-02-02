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
            _score[score.dashboardId] = score;
        }


        this.setScore = function (dashboardId, score) {
            _score[dashboardId] = score;
        }

        this.getScore = function (dashboardId) {
          var score = _score[dashboardId];
          if (!score) {
            return null;
          }
          return score;
        }

        this.getScoreByDashboardWidget = function (dashboardId, widgetId) {
            var score = _score[dashboardId];
            if (!score) {
                return null;
            }
            return _.find(score.scoreWidgetMetrics, {id: widgetId});
        }
    }
})();
