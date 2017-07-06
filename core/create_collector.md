Create New Collector
==================================================

Copy skeleton collector project
--------------------------------------
Copy the sample collector project into a new directory in the collectors folder and give it a name to match the collector
you are building (eg pivotalTracker). In the pom.xml file, change the artifactId, name and optionally the groupId elements, like so:

    <project>
        <groupId>com.capitalone.dashboard</groupId>
        <artifactId>pivotal-tracker-collector</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <name>Pivotal Tracker Collector</name>

        ...
    </project>

The pom file includes all of the dependencies you will need to interact with the Dashboard Application MongoDB instance,
such as Model classes and Repositories.

Extending Collector
--------------------------------------

If you need to store extra configuration for your collector, you can create a class that extends Collector and add new
fields to store configuration information that is specific to your collector.

    package com.capitalone.dashboard.collector;

    import com.capitalone.dashboard.model.Collector;

    /**
     * Collect Features from PivotalTracker
     */
    public class PivotalTrackerCollector extends Collector {
        private String apiToken;

        public String getApiToken() {
            return apiToken;
        }

        public void setApiToken(String apiToken) {
            this.apiToken = apiToken;
        }
    }

If you decide to take this approach, you will also need to create a Spring Data repository interface that extends from
BaseCollectorRepository.

    package com.capitalone.dashboard.repository;

    import com.capitalone.dashboard.model.PivotalTrackerCollector;

    public interface PivotalTrackerCollectorRepository extends BaseCollectorRepository<PivotalTrackerCollector> {
    }


Extending CollectorItem
--------------------------------------

Create a class that extends CollectorItem and add new fields to store information that identifies a unique item within
the tool. In the Pivotal Tracker example, we will add a projectId field to CollectorItem so that each project's features
can be tracked.

    package com.capitalone.dashboard.collector;

    import com.capitalone.dashboard.model.CollectorItem;

    public class PivotalTrackerCollectorItem extends CollectorItem {
        private long projectId;

        public long getProjectId() {
            return projectId;
        }

        public void setProjectId(long projectId) {
            this.projectId = projectId;
        }
    }


Create CollectorTask
--------------------------------------

Create a class that extends the abstract CollectorTask and implement the required abstract methods.


### getCollector() Method
--------------------------------------

The getCollector method should return a prototypical instance of your Collector subclass (eg PivotalTrackerCollector). This
method is only used the very first time your collector runs so that your collector instance can be registered in the collectors
collection in MongoDB.


### getCollectorRepository() Method
--------------------------------------

This method should return a reference to your custom collector repository.

### getCron() Method
--------------------------------------

This method should return the cron expression to schedule how often your collector executes.

### collect() Method
--------------------------------------

The collect method holds the business logic for your collector. This method is called on a schedule based on the value you
provide from the getCron() method..


### Spring Singleton
--------------------------------------

The CollectorTask class is a Spring bean singleton. Use the constructor to inject any Spring beans that are required to
execute the logic of your collector (eg MongDB repositories such as FeatureRepository).

### Example
--------------------------------------

    package com.capitalone.dashboard.collector;

    /**
     * Collects Features from Pivotal Tracker
     */
    public class PivotalTrackerCollectorTask extends CollectorTask<PivotalTrackerCollector> {

        private final FeatureRepository featureRepository;
        private final PivotalTrackerCollectorRepository pivotalTrackerCollectorRepository;

        @Value("${cron}") // Injected from application.properties
        private String cron;

        @Value("${apiToken}") // Injected from application.properties
        private String apiToken

        @Autowired
        public PivotalTrackerCollectorTask(TaskScheduler taskScheduler,
                                            FeatureRepository featureRepository,
                                            PivotalTrackerCollectorRepository pivotalTrackerCollectorRepository) {
            super(taskScheduler, "Pivotal Tracker");
            this.featureRepository = featureRepository;
            this.pivotalTrackerCollectorRepository = pivotalTrackerCollectorRepository;
        }

        @Override
        public PivotalTrackerCollector getCollector() {

            PivotalTrackerCollector collector = new PivotalTrackerCollector();

            collector.setName("Pivotal Tracker"); // Must be unique to all collectors for a given Dashboard Application instance
            collector.setCollectorType(CollectorType.Feature);
            collector.setEnabled(true);
            collector.setApiToken(apiToken);

            return collector;
        }

        @Override
        public BaseCollectorRepository<PivotalTrackerCollectorRepository> getCollectorRepository() {
            return pivotalTrackerCollectorRepository;
        }

        @Override
        public String getCron() {
            return cron;
        }

        @Override
        public void collect(PivotalTrackerCollector collector) {

            // Collector logic
            PivotalTrackerApi api = new PivotalTrackerApi(collector.getApiToken());

            for (Project project : api.getProjects()) {

                PivotalTrackerCollectorItem collectorItem = getOrCreateCollectorItems(project.getProjectId());

                // Naive implementation
                deleteFeaturesFor(collectorItem);

                addFeaturesFor(collectorItem, project.getStories());
            }
        }

        private PivotalTrackerCollectorItem getOrCreateCollectorItem(long projectId) {
            // ...
        }

        private void deleteFeaturesFor(PivotalTrackerCollectorItem collectorItem) {
            // ...
        }

        private void addFeaturesFor(PivotalTrackerCollectorItem collectorItem, List<Story> stories) {
            // ...
        }
    }


Building and Deploying
--------------------------------------

Run mvn install to package the collector into an executable JAR file. Copy this file to your server and launch it using
java -JAR pivotal-tracker-collector.jar. You will need to provide an application.properties file that contains information about how
to connect to the Dashboard MongoDB database instance, as well as any custom properties that your collector requires. See
the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files)
for information about sourcing this properties file.


### Sample application.properties file
--------------------------------------

    #Database Name
    spring.data.mongodb.database=dashboard

    #Database HostName could also be ip address
    spring.data.mongodb.host=localhost

    #Database Port
    spring.data.mongodb.port=27017

    #Database Username
    spring.data.mongodb.username=foo

    #Database Password
    spring.data.mongodb.password=bar

    #Collector schedule
    cron=* 30 * * * *

    #Pivotal Tracker Token
    apiToken=HJBKJHG76JHG%^$^jhJH



