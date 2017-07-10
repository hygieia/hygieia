/**
* Truncates texts based on given length
*/
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .filter('trunText', trunText);

    function trunText() {
        return function (text, length, end) {
            if (isNaN(length))
                length = 10;

            if (end === undefined)
                end = "...";

            if (text.length <= length || text.length - end.length <= length) {
                return text;
            }
            else {
                return String(text).substring(0, length-end.length) + end;
            }

        };
    }

})();