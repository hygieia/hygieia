package com.capitalone.dashboard.repository;

import org.springframework.context.annotation.Bean;

import com.capitalone.dashboard.config.MongoConfig;
import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;

public class FongoConfig extends MongoConfig {

    @Override
    @Bean
    public MongoClient mongo() throws Exception {
        return new Fongo(getDatabaseName()).getMongo();
    }
    
    @Override
    protected String getDatabaseName() {
        return "test-db";
    }
}
