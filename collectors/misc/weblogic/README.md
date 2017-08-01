#This collector queries weblogic REST API to get the server health information
#Before running this collector, make sure weblogic configuration is changed to enable REST based querying and restart weblogic

#http://www.oracle.com/technetwork/articles/soa/oliveira-wls-rest-javafx-1723982.html
#you can query the following endpoint in your browser, should return a json if successful
#http://yourweblogichost:port/management/tenant-monitoring/servers
#this collector is similar to Jenkins Build collector, it will query only those domains configured in the widget

#you can configure the weblogic widget in the UI ONLY after running the collector and loading the collector items.
#UI configuration supports type ahead feature, so if you don't get suggestions, then your collector_items (weblogic domains) are not loaded


# Default server configuration
debug=false
spring.jpa.show-sql: false

# Default server configuration values
server.contextPath=/api
server.port=${PORT:0}


# dashboard.properties
dbname=dashboard
dbusername=dashboard
dbpassword=dashboard
dbhost=localhost
dbport=27017


weblogic.cron=0 0/15 * * * *

#Credentials
weblogic.username=weblogicUserName
weblogic.password=weblogicPassword

#replace these domain name | address <--->url:port mappings with yours, any number of domains can be  used
weblogic.servers[0] = dev1web|http://host:port
weblogic.servers[1] = dev1service|http://host:port
weblogic.servers[2] = dev1soa|http://host:port

