package com.capitalone.dashboard.config;

import com.capitalone.dashboard.repository.RepositoryPackage;
import com.capitalone.dashboard.util.DefaultPropertiesSupplier;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClient;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Properties;

@EnableMongoRepositories(basePackageClasses = RepositoryPackage.class)
public class MongoConfig extends AbstractMongoConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoConfig.class);

    private final String databaseName;
    private final String host;
    private int port;
    private final String userName;
    private final char[] password;


    public MongoConfig() {
        Properties properties = new DefaultPropertiesSupplier().get();
        databaseName = properties.getProperty("dbname", "dashboard");
        host = properties.getProperty("dbhost", "localhost");
        port = Integer.parseInt( properties.getProperty("dbport", "27017") );
        userName = properties.getProperty("dbusername", "");
        password = properties.getProperty("dbpassword", "").toCharArray();
        System.out.println("dbusername=" + userName + ", password=" + password.toString());
    }

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    @Bean
    public MongoClient mongo() throws Exception {
        LOGGER.info("Using Mongo host: " + host + " port: " + port + " userName: " + userName + " databaseName: " + databaseName);

        if (StringUtils.isEmpty(userName)) {
            return new MongoClient(new ServerAddress(host, port));
        }

		MongoCredential mongoCredential = MongoCredential.createMongoCRCredential(userName, databaseName, password);
		return new MongoClient(new ServerAddress(host, port), Arrays.asList(mongoCredential));
    }

    @Override
    protected String getMappingBasePackage() {
        return "com.capitalone.dashboard.model";
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), getDatabaseName());
    }

}