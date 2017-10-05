/**
 * Gets template related data
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module + '.core')
        .factory('templateMangerData', templateMangerData);

    function templateMangerData($http) {
        var createTemplateRoute = '/api/template/';
        var getTemplatesRoute = '/api/templates';

        return {
            search: search,
            createTemplate: createTemplate,
            getAllTemplates: getAllTemplates,
            deleteTemplate: deleteTemplate,
            updateTemplate: updateTemplate
        };

        function search(template) {
            return $http.get(createTemplateRoute + template)
                .then(function (response) {
                    return response.data;
                });
        }

        function getAllTemplates() {
            return $http.get(getTemplatesRoute)
                .then(function (response) {
                    return response.data;
                });
        }

        // creates a new template
        function createTemplate(data) {
            return $http.post(createTemplateRoute, data)
                .success(function (response) {
                    return response.data;
                })
                .error(function (response) {
                    return null;
                });
        }

        // deletes a Template
        function deleteTemplate(id) {
            return $http.delete(createTemplateRoute + '/' + id)
                .then(function (response) {
                    return response.data;
                });
        }

        function updateTemplate(id, data) {
            return $http.put(createTemplateRoute + '/' + id, data)
                .then(function (response) {
                    return response.data;
                });
        }

    }
})();