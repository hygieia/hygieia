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
            var businessServiceName = data.configurationItemBusServName ? "-" + data.configurationItemBusServName : "";
            var businessApplicationName = data.configurationItemBusAppName ? "-" + data.configurationItemBusAppName : "";
            var applicationName = data.application && data.application.name ? "-" + data.application.name : "" ;

            if(businessServiceName != "" || businessApplicationName != "" ){
               title = title +  businessServiceName + businessApplicationName;
            }else{
               title = title + applicationName;
            }

            return title;
        }
        this.getDashboardTitleOrig = function(data){
            var subName = data.name.substring(0, data.name.indexOf('-'));

            return subName ? subName : data.name
        }
        this.getBusSerToolTipText = function (){
            return "A top level name which support Business function."
        }

        this.getBusAppToolTipText = function (){
            return " A Business Application (BAP) CI is a CI Subtype in the application which supports business function (Top level)."
        }
    }
})();