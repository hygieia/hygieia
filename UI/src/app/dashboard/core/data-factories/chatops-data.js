/**
 * Gets code repo related data
 */
(function () {
    'use strict';

    angular
        .module('devops-dashboard.core')
        .factory('chatOpsData', chatOpsData);

    function chatOpsData($http) {
        var testDetailRoute = 'test-data/chatops-hipchat.json';
        var testImgDetailRoute = 'test-data/chatops-hipchat-img.json';



        return {
            details: details
        };

        function details(serviceUrl) {
            console.log("ServiceURl:"+serviceUrl);
            return $http.get(serviceUrl).then(function (response) {

                    return response.data;
                },function(response){
                console.log("Error occured:"+JSON.stringify(response));
                return response.data;
            });
        }



    }

})();