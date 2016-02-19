(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .controller('ChatOpsViewController', ChatOpsViewController)
        .filter('unsafe', function ($sce) {
            return function (val) {
                return $sce.trustAsHtml(val);
            };
        });

    ChatOpsViewController.$inject = ['$q', '$scope', 'chatOpsData'];
    function ChatOpsViewController($q, $scope, chatOpsData) {
        var ctrl = this;

        //Get the stored dashboard configuration for this dashboard


        ctrl.chatOpsRoomAuthToken = $scope.widgetConfig.options.chatOpsRoomAuthToken;
        ctrl.chatOpsServerUrl = $scope.widgetConfig.options.chatOpsServerUrl;
        ctrl.chatOpsRoomName = $scope.widgetConfig.options.chatOpsRoomName;
        ctrl.messageArray = "";
        ctrl.showMessages = false;
        ctrl.apiErrorOccured=false;

        var offset = new Date().getTimezoneOffset();
        var tz = jstz.determine(); // Determines the time zone of the browser client
        var completeUrl = ctrl.chatOpsServerUrl + "/v2/room/" + ctrl.chatOpsRoomName + "/history/latest?timezone=" + tz.name() + "&max-results=5&auth_token=" + ctrl.chatOpsRoomAuthToken;

        ctrl.load = function () {
            var deferred = $q.defer();

            chatOpsData.details(completeUrl).then(function (data) {
                if (typeof data.error != 'undefined') {
                    ctrl.apiErrorOccured=true;
                    ctrl.messageArray=data;
                }
                else {
                    processResponse(data);
                    //deferred.resolve(data.lastUpdated);
                }

            });
            ctrl.showMessages = true;
            return deferred.promise;
        };

        function processResponse(data) {
            var messageArray = data.items;
            ctrl.messageArray = messageArray;
        };


        ctrl.replaceURL = function (mytext) {
            var exp = /(\b(https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig;
            return mytext.replace(exp, "<a href='$1'><span class='chat-link'>Link</span></a>");
        };
    }

})();
