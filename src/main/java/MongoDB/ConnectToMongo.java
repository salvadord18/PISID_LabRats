package MongoDB;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConnectToMongo {

    MongoClient mongoClient;
    String mongo_database;

    public ConnectToMongo() throws IOException {
        Properties p = new Properties();
        p.load(new FileInputStream("cloudToMongo.ini"));
        var mongo_address = p.getProperty("mongo_address");
        var mongo_user = p.getProperty("mongo_user");
        var mongo_password = p.getProperty("mongo_password");
        var mongo_replica = p.getProperty("mongo_replica");
        this.mongo_database = p.getProperty("mongo_database");
        var mongo_authentication = p.getProperty("mongo_authentication");

        String mongoURI = "mongodb://";
        if (mongo_authentication.equals("true")) mongoURI = mongoURI + mongo_user + ":" + mongo_password + "@";
        mongoURI = mongoURI + mongo_address;
        if (!mongo_replica.equals("false"))
            if (mongo_authentication.equals("true"))
                mongoURI = mongoURI + "/?replicaSet=" + mongo_replica + "&authSource=admin";
            else mongoURI = mongoURI + "/?replicaSet=" + mongo_replica;
        else if (mongo_authentication.equals("true")) mongoURI = mongoURI + "/?authSource=admin";

        MongoClientURI connectionMongo  = (new MongoClientURI(mongoURI));
        this.mongoClient = new MongoClient(connectionMongo);
    }

    public MongoClient getConnectionMongo(){

        return this.mongoClient;
    }

    public DB getDataBase(){
        return this.getConnectionMongo().getDB(this.mongo_database);
    }



}
