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
            return JSON.parse('[{"id":"571f9af9ed678095d297aaca","instanceId":"i-5b5f99c6","accountNumber":"685250009713","instanceType":"m3.medium","imageId":"ami-7881df12","imageExpirationDate":1451624580000,"imageApproved":true,"privateDns":"ip-10-205-75-142.dqa.capitalone.com","privateIp":"10.205.75.142","publicDns":"","subnetId":"subnet-c96114e2","virtualNetworkId":"vpc-f98ff19c","age":0,"cpuUtilization":0,"lastUpdatedDate":0,"securityGroups":["ISRM-Base-SG-Dev-DbSG-G8KE44V2LGAZ"],"tags":[{"name":"OwnerContact","value":"rAPId200@capitalone.com"},{"name":"aws:autoscaling:groupName","value":"papi-ipo-pgclient-dev-app-as"},{"name":"ASV","value":"ASVPARTNERSHIPAPI"},{"name":"Name","value":"papi-ipo-pgclient-dev"},{"name":"OwnerEID","value":"IXR303"},{"name":"CMDBEnvironment","value":"ENVNPINTPARTNEROFFERS"}],"networkIn":0,"networkOut":0,"diskRead":0,"diskWrite":0,"rootDeviceName":"/dev/sda1","encrypted":false,"monitored":false,"stopped":false,"tagged":false},{"id":"571f9af9ed678095d297aacb","instanceId":"i-1b70c286","accountNumber":"685250009713","instanceType":"m4.large","imageId":"ami-7526281f","imageExpirationDate":1451624760000,"imageApproved":true,"privateDns":"ip-10-205-86-214.dqa.capitalone.com","privateIp":"10.205.86.214","publicDns":"","subnetId":"subnet-3ce08e17","virtualNetworkId":"vpc-f98ff19c","age":0,"cpuUtilization":0,"lastUpdatedDate":0,"securityGroups":["ISRM-Base-SG-Dev-AppSG-1VSF9N00I6CRL","ISRM-Base-SG-Dev-DbSG-G8KE44V2LGAZ"],"tags":[{"name":"aws:cloudformation:stack-id","value":"arn:aws:cloudformation:us-east-1:685250009713:stack/CARD-ATeam-RPM-LG/1f17af90-07e5-11e6-948b-50d5cad95262"},{"name":"Name","value":"CARD-ATeam-RPM-LG"},{"name":"visigoths:nott","value":"exclude"},{"name":"CMDBEnvironment","value":"ENVPERFORMANCECENTER"},{"name":"aws:cloudformation:logical-id","value":"RPMInstance"},{"name":"ASV","value":"ASVPERFORMANCECENTER"},{"name":"aws:cloudformation:stack-name","value":"CARD-ATeam-RPM-LG"},{"name":"OwnerContact","value":"tqh320"}],"networkIn":0,"networkOut":0,"diskRead":0,"diskWrite":0,"rootDeviceName":"/dev/sda1","encrypted":false,"monitored":false,"stopped":false,"tagged":false},{"id":"571f9af9ed678095d297aacc","instanceId":"i-59ad37c2","accountNumber":"685250009713","instanceType":"c4.4xlarge","imageId":"ami-dd2576b8","imageExpirationDate":0,"imageApproved":false,"privateDns":"ip-10-205-83-233.dqa.capitalone.com","privateIp":"10.205.83.233","publicDns":"","subnetId":"subnet-9cdf9ac5","virtualNetworkId":"vpc-f98ff19c","age":0,"cpuUtilization":0,"lastUpdatedDate":0,"securityGroups":["ISRM-Base-SG-Dev-DbSG-G8KE44V2LGAZ"],"tags":[{"name":"OwnerEID","value":"GOQ689"},{"name":"aws:autoscaling:groupName","value":"SparkCOP-20151215-HDFSStack-14UNBBGSLK8MA-HDFSASGStack-166UR8NYHWX1V-HDFSSlavesASG-1GY6QVGEBBYYM"},{"name":"OwnerEmail","value":"Saurabh.Gupte@capitalone.com"},{"name":"StackName","value":"SparkCOP-20151215-HDFSStack-14UNBBGSLK8MA-HDFSASGStack-166UR8NYHWX1V"},{"name":"aws:cloudformation:stack-name","value":"SparkCOP-20151215-HDFSStack-14UNBBGSLK8MA-HDFSASGStack-166UR8NYHWX1V"},{"name":"CMDBEnvironment","value":"ENVNPCUSTOMERMANAGEMENTCEP"},{"name":"SystemTeam","value":"MadMod-MadScientists"},{"name":"aws:cloudformation:logical-id","value":"HDFSSlavesASG"},{"name":"aws:cloudformation:stack-id","value":"arn:aws:cloudformation:us-east-1:685250009713:stack/SparkCOP-20151215-HDFSStack-14UNBBGSLK8MA-HDFSASGStack-166UR8NYHWX1V/731a9980-a337-11e5-8bf3-5044330e9836"},{"name":"SNSTopicARN","value":"arn:aws:sns:us-east-1:685250009713:CEPAPP-test-AlertNotify"},{"name":"FeatureTeam","value":"MadMod-HeartBeat"},{"name":"Name","value":"QuantumApp-HDFSSlaveInstance"}],"networkIn":0,"networkOut":0,"diskRead":0,"diskWrite":0,"rootDeviceName":"/dev/sda1","encrypted":false,"monitored":false,"stopped":false,"tagged":false}]');
            return $http.get(HygieiaConfig.local ? testDataRoute : cloudDataRoute)
                .then(function (response) {
                    return response.data[0].result;
                });
        }
   }
})();