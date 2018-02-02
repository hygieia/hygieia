(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('scoreConfigController', scoreConfigController);

    scoreConfigController.$inject = ['modalData', 'collectorData','$uibModalInstance'];
    function scoreConfigController(modalData, collectorData, $uibModalInstance) {
        /*jshint validthis:true */
        var ctrl = this;
        //Cache all apps data

        var widgetConfig = modalData.widgetConfig;

        // public variables
        ctrl.jobDropdownDisabled = true;
        ctrl.jobDropdownPlaceholder = 'Loading...';
        ctrl.submitted = false;
        ctrl.collectors = [];
        ctrl.collectorItemId = null;
        ctrl.settingsType = 'DEFAULT';
        ctrl.buildWidget = null;
        ctrl.qualityWidget = null;
        ctrl.deployWidget = null;
        // public methods
        ctrl.submit = submit;
        ctrl.isSettingCustom = isSettingCustom;
        ctrl.settingsTypeChange = settingsTypeChange;

        init();

        function init() {
            // Request collecters
            collectorData.collectorsByType('score').then(processCollectorsResponse);
            if (widgetConfig && widgetConfig.options) {
                getScoreItemForWidget().then(initializeWidgetData)
            }
        }

        function isSettingCustom() {
            return (ctrl.settingsType === 'CUSTOM');
        }

        function settingsTypeChange() {
            if (ctrl.settingsType === 'CUSTOM') {
                if (!ctrl.buildWidget && !ctrl.qualityWidget && !ctrl.deployWidget) {
                    ctrl.buildWidget = {
                        weight: 34,
                        numberOfDays: 14,
                        status: {
                            weight: 50
                        },
                        duration: {
                            weight: 50,
                            buildDurationThresholdInMillis: 300000
                        }
                    };

                    ctrl.deployWidget = {
                        intancesOnline: {
                            weight: 50
                        },
                        deploySuccess: {
                            weight: 50
                        },
                        weight: 33
                    };

                    ctrl.qualityWidget = {
                        unitTests: {
                           weight: 50
                        },
                        codeCoverage: {
                           weight: 50
                        },
                        weight: 33
                    };
                }
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

            if (ctrl.collectorItemId) {
                ctrl.settingsType = ctrl.collectorItemId.options.settingsType;
                ctrl.buildWidget = ctrl.collectorItemId.options.buildWidget;
                ctrl.qualityWidget = ctrl.collectorItemId.options.qualityWidget;
                ctrl.deployWidget = ctrl.collectorItemId.options.deployWidget;
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
                    dashboardId: modalData.dashboard.id,
                    settingsType: ctrl.settingsType,
                    buildWidget: ctrl.buildWidget,
                    qualityWidget: ctrl.qualityWidget,
                    deployWidget: ctrl.deployWidget
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
