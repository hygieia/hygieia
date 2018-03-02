(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('scoreConfigController', scoreConfigController);

    scoreConfigController.$inject = ['modalData', 'collectorData','$uibModalInstance'];
    function scoreConfigController(modalData, collectorData, $uibModalInstance) {
        /*jshint validthis:true */
        var ctrl = this;

        var widgetConfig = modalData.widgetConfig;

        // public variables
        ctrl.submitted = false;
        ctrl.collectors = [];
        ctrl.collectorItemId = null;
        // public methods
        ctrl.submit = submit;

        init();

        function init() {
            // Request collecters
            collectorData.collectorsByType('score').then(processCollectorsResponse);
            if (widgetConfig && widgetConfig.options) {
                getScoreItemForWidget().then(initializeWidgetData)
            }
        }

        function processCollectorsResponse(data) {
            ctrl.collectors = data;
        }

        function initializeWidgetData(data) {
            if (data && data.length > 0) {
                ctrl.collectorItemId = data[0];
            } else {
                ctrl.collectorItemId = null;
            }
        }

        function getScoreItemForWidget() {
            return collectorData.itemsByType('score', {"search": modalData.dashboard.id, "size": 20}).then(function (response){
                return response;
            });
         }


        function submit(valid) {
            ctrl.submitted = true;

            if (!valid) {
                return;
            }

            if (ctrl.collectorItemId) {
                updateCollectorItem().then(saveWidget);
            } else {
                createCollectorItem().then(saveWidget);
            }
        }

        function saveWidget(response) {
            var postObj = {
                name: 'score',
                options: {
                        id: widgetConfig.options.id,
                        dashboardId: modalData.dashboard.id
                },
                componentId: modalData.dashboard.application.components[0].id,
                collectorItemId : response.data.id
            };

            $uibModalInstance.close(postObj);
        }

        function getScoreItemModel() {
            return  {
                collectorId: _.find(ctrl.collectors, {name: 'Score'}).id,
                description: modalData.dashboard.id,
                options: {
                    dashboardId: modalData.dashboard.id
              }
            };
        }

        function createCollectorItem() {
            var item = getScoreItemModel();

            return collectorData.createCollectorItem(item);
        }

        function updateCollectorItem() {
            var item = getScoreItemModel();

            return collectorData.updateCollectorItem(ctrl.collectorItemId.id, item);
        }
    }
})();
