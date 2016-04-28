/**
 * Created by nmande on 4/12/16.
 */

(function () {
    'use strict';


    angular
        .module(HygieiaConfig.module + '.core')
        .factory('cloudData', cloudData);

    function cloudData($http) {

        var testDataRoute = 'asv_data.json';
        var cloudDataRoute = '/api/asv/';

        return {
            getAccounts: getAccounts,
            getAWSGlobalData: getAWSGlobalData,
            getAWSInstancesByTag: getAWSInstancesByTag
        };

        function getAccounts() {
            return JSON.parse('[{ "name": "Development Account"},{ "name": "Production Account"}]');
            return $http.get(HygieiaConfig.local ? testDataRoute : cloudDataRoute)
                .then(function (response) {
                    return response.data[0].result;
                });
        }


        function getAWSGlobalData() {


            /* return {
                "compute": {
                    "ec2Instances": 3015,
                    "running": 1900,
                    "stopped": 300,
                    "excluded": 910
                },
                "s3": {
                    "s3Buckets": 9000,
                    "encrypted": 35,
                    "tagged": 45,
                    "compliant": 54
                }
            }; */
        }

        function getAWSInstancesByTag(tag, value) {
            return JSON.parse('[{"id":"571f9af9ed678095d297b06b","instanceId":"i-d6d9824e","accountNumber":"685250009713","instanceType":"m4.large","imageId":"ami-f7cc809d","imageExpirationDate":0,"imageApproved":false,"privateDns":"ip-10-205-81-186.dqa.capitalone.com","privateIp":"10.205.81.186","publicDns":"","subnetId":"subnet-9cdf9ac5","virtualNetworkId":"vpc-f98ff19c","age":0,"cpuUtilization":1.8593333333333333,"lastUpdatedDate":1461688452352,"securityGroups":["ISRM-Base-SG-Dev-DbSG-G8KE44V2LGAZ"],"tags":[{"name":"CMDBEnvironment","value":"ENVCARDNPPAYMENTS"},{"name":"aws:cloudformation:logical-id","value":"MongoDB1"},{"name":"ChefEnvironment","value":"efit_ei_dev_migration"},{"name":"OwnerContact","value":"arn:aws:sns:us-east-1:685250009713:EFIT_Migration"},{"name":"aws:cloudformation:stack-name","value":"EFIT-MIG-Automatic12"},{"name":"Name","value":"EFIT-MIG-Automatic12"},{"name":"ASV","value":"ENVCARDNPPAYMENTS"},{"name":"SNSTopicARN","value":"arn:aws:sns:us-east-1:685250009713:EFIT_Migration"},{"name":"EID","value":"<<<<<<<ENTERyourEID>>>>>>>"},{"name":"ChefRole","value":"app_test"},{"name":"aws:cloudformation:stack-id","value":"arn:aws:cloudformation:us-east-1:685250009713:stack/EFIT-MIG-Automatic12/73449160-da3a-11e5-b7b8-50fae9e50c9a"}],"networkIn":72040.41666666667,"networkOut":123873.31666666667,"diskRead":0,"diskWrite":0,"rootDeviceName":"/dev/sda1","encrypted":true,"monitored":false,"stopped":false,"tagged":true}]');
            return $http.get(HygieiaConfig.local ? testDataRoute : cloudDataRoute)
                .then(function (response) {
                    return response.data[0].result;
                });
        }
   }
})();