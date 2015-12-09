(function () {
    'use strict';

    angular
        .module('devops-dashboard')
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
        console.log("Client timezone:" + offset);
        var tz = jstz.determine(); // Determines the time zone of the browser client
        var completeUrl = ctrl.chatOpsServerUrl + "/v2/room/" + ctrl.chatOpsRoomName + "/history/latest?timezone=" + tz.name() + "&max-results=5&auth_token=" + ctrl.chatOpsRoomAuthToken;

        console.log("Complete URl is :" + completeUrl);


        ctrl.load = function () {
            console.log("In load");
            var deferred = $q.defer();

            chatOpsData.details(completeUrl).then(function (data) {

                console.log("DATA GOT BACK:" + JSON.stringify(data));
                if (typeof data.error != 'undefined') {
                    ctrl.apiErrorOccured=true;
                    ctrl.messageArray=data;
                }
                else {
                    processResponse(data);
                }

            });
            ctrl.showMessages = true;
            return deferred.promise;
        };

        function processResponse(data) {

            console.log("Data Items:" + JSON.stringify(data.items));

            var messageArray = data.items;
            ctrl.messageArray = messageArray;
            console.log("length of array" + messageArray.length);
        };


        ctrl.replaceURL = function (mytext) {

            console.log("Text received is :" + mytext);
            var exp = /(\b(https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig;
            return mytext.replace(exp, "<a href='$1'><span class='chat-link'>Link</span></a>");
        };

        ctrl.getLocalTime = function (chatTimeStamp) {
            var chatTimeStamps = chatTimeStamp;
            var hour = new Date(chatTimeStamps).getHours();
            var min = new Date(chatTimeStamps).getMinutes();
            var sec = new Date(chatTimeStamps).getSeconds();
            if (sec < 10) {
                sec = "0" + sec;
            }
            if (hour < 10) {
                hour = "0" + hour;
            }
            if (min < 10) {
                min = "0" + min;
            }
            var messdates = hour + ":" + min + ":" + sec;
            return messdates;
        };


        ctrl.getImageUrl = function (hipchatuserid) {
            var imageRestApiUrl = ctrl.chatOpsServerUrl + "/v2/user/" + hipchatuserid + "?auth_token=" + ctrl.chatOpsRoomAuthToken;
            console.log("Image URL:" + imageRestApiUrl);
            return imageRestApiUrl;

        };

        ctrl.getImage = function (imageRestApiUrl) {
            console.log("I am getting called for each message");
        };


    }

})();
