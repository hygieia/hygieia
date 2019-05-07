package com.capitalone.dashboard.config;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;

public class FongoConfig extends MongoConfig {

    @Override
    @Bean
    public MongoClient mongo()  {
        return new Fongo(getDatabaseName()).getMongo();
    }
    
    @Override
    protected String getDatabaseName() {
        return "test-db";
    }
}
