package com.capitalone;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by Mike Peretz on 6/29/2016.
 */
public class ADWidgetApi {


    public static void main(String[] args) {


        new ADWidgetApi().buildDB(new MetricObject().initialize("996"));
    }

    private void buildDB(MetricObject metricObject) {


        MongoCredential credential = MongoCredential.createCredential("db", "dashboard", "dbpass".toCharArray());
        MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 27017), Arrays.asList(credential));
        MongoDatabase db = mongoClient.getDatabase("dashboard");
        MongoCollection<Document> collection = db.getCollection("metrics");
        collection.drop();


        Map<String, Double> metricDataMap = metricObject.getMetricDataMap();

        Document doc = new Document();

        for (Map.Entry<String, Double> entry : metricDataMap.entrySet())
            doc.append(entry.getKey(), entry.getValue());

        collection.insertOne(doc);

        mongoClient.close();

    }


}
