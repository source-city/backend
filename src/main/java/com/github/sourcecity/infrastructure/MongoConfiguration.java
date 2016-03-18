package com.github.sourcecity.infrastructure;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import static com.mongodb.MongoCredential.createCredential;
import static java.util.Collections.singletonList;

@Configuration
public class MongoConfiguration extends AbstractMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return "heroku_n0tdfl82";
    }

    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient(
                singletonList(new ServerAddress("ds013579.mlab.com", 13579)),
                singletonList(createCredential("source-city-app",
                                               "heroku_n0tdfl82",
                                               "source-city-app".toCharArray())));
    }
}
