/**
 * Service to handle Dashboard operations
 */
(function () {
    'use strict';

    angular
        .module(HygieiaConfig.module)
        .service('dashboardService', dashboardService);
    dashboardService.$inject = ['DashboardType' , 'dashboardData'];
    function dashboardService(DashboardType, dashboardData) {
        var businessApplicationId;
        var businessServiceId;
        var getDashboardType = function(){
            return DashboardType;
        }

        this.getBusServValueBasedOnType = function(dashboardType, value){
            return dashboardType === getDashboardType().PRODUCT ? "" : value;
        }

        this.setBusinessServiceId = function(id){
            businessServiceId = id;
        }
        this.setBusinessApplicationId = function(id){
            businessApplicationId = id;
        }
        this.getBusinessServiceId = function(name){
            var value = null;
            if(name){
                value = businessServiceId;
            }
            return value;
        }
        this.getBusinessApplicationId = function(name){
            var value = null;
            if(name){
                value = businessApplicationId;
            }
            return value;
        }

        this.getDashboardTitle = function (data) {
            var title = data.title;
            var businessServiceName = data.configurationItemAppName ? " - " + data.configurationItemAppName : "";
            var businessApplicationName = data.configurationItemCompName ? " - " + data.configurationItemCompName : "";
            var applicationName = data.application.name ? " - " + data.application.name : "" ;

            if(businessServiceName != "" && businessApplicationName != "" ){
               title = title +  businessServiceName + businessApplicationName;
            }else{
               title = title + applicationName;
            }

            return title;
        }

        this.getBusSerToolTipText = function (){
            return "getBusSerToolTipText tooltip test text!"
        }

        this.getBusAppToolTipText = function (){
            return "getBusAppToolTipText tooltip test text!"
        }
    }
})();