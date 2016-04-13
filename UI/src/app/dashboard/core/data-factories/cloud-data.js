/**
 * Created by hyw912 on 4/12/16.
 */

(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('cloudData', cloudData);

    function cloudData() {

        return {
            getData: getData
        };

        function getData() {
            return "hello";
        }
    }
})();