/**
 * Build widget configuration
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('ChatOpsConfigController', ChatOpsConfigController);

    ChatOpsConfigController.$inject = ['modalData', '$uibModalInstance',
        'collectorData'];
    function ChatOpsConfigController(modalData, $uibModalInstance, collectorData) {
        var ctrl = this;
        var widgetConfig = modalData.widgetConfig;

        ctrl.chatOpsOptions = [{
            name: 'HipChat',
            value: 'HipChat'
        }, {
            name: 'Slack',
            value: 'Slack (Not implemented)'
        }, {
            name: 'Gitter',
            value: 'Gitter (Not implemented)'
        }];


        if (!widgetConfig.options.chatOpsOption) {
            ctrl.chatOpsOption = "";
        }
        else {
            var myindex;

            for (var v = 0; v < ctrl.chatOpsOptions.length; v++) {
                if (ctrl.chatOpsOptions[v].name == widgetConfig.options.chatOpsOption.name) {
                    myindex = v;
                    break;
                }
            }
            ctrl.chatOpsOption = ctrl.chatOpsOptions[myindex];
        }

        ctrl.chatOpsRoomName=widgetConfig.options.chatOpsRoomName;
        ctrl.chatOpsRoomAuthToken=widgetConfig.options.chatOpsRoomAuthToken;
        ctrl.chatOpsServerUrl=widgetConfig.options.chatOpsServerUrl;



        // public variables
        ctrl.submitted = false;
        ctrl.collectors = [];


        // public methods
        ctrl.submit = submitForm;

        // Request collecters
        collectorData.collectorsByType('ChatOps').then(processCollectorsResponse);

        function processCollectorsResponse(data) {
            ctrl.collectors = data;
        }

        /*
         * function submitForm(valid, url) { ctrl.submitted = true; if (valid &&
         * ctrl.collectors.length) {
         * createCollectorItem(url).then(processCollectorItemResponse); } }
         */

        function submitForm(valid, chatOpsOption, chatOpsRoomAuthToken, chatOpsServerUrl, chatOpsRoomName) {
            ctrl.submitted = true;
            if (valid && ctrl.collectors.length) {

                    createCollectorItem(chatOpsOption, chatOpsRoomAuthToken, chatOpsServerUrl, chatOpsRoomName).then(
                        processCollectorItemResponse);


                }
            }



        function createCollectorItem(chatOpsOption, chatOpsRoomAuthToken, chatOpsServerUrl, chatOpsRoomName) {
            var item = {
                    collectorId: _.find(ctrl.collectors, {name: 'ChatOps'}).id,
                    options: {
                        chatOpsOption: chatOpsOption,
                        chatOpsRoomAuthToken: chatOpsRoomAuthToken,
                        chatOpsServerUrl: chatOpsServerUrl,
                        chatOpsRoomName: chatOpsRoomName
                    }
                };


            return collectorData.createCollectorItem(item);
        }

        function processCollectorItemResponse(response) {
            var postObj = {
                name: "ChatOps",
                options: {
                    id: widgetConfig.options.id,
                    chatOpsOption: ctrl.chatOpsOption,
                    chatOpsRoomName:ctrl.chatOpsRoomName,
                    chatOpsRoomAuthToken: ctrl.chatOpsRoomAuthToken,
                    chatOpsServerUrl: ctrl.chatOpsServerUrl
                },
                componentId: modalData.dashboard.application.components[0].id,
                collectorItemId: response.data.id
            };

            // pass this new config to the modal closing so it's saved
            $uibModalInstance.close(postObj);
        }
    }
})();