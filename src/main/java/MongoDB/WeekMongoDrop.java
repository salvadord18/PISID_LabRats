package MongoDB;

import com.mongodb.DB;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;

@RequiredArgsConstructor
public class WeekMongoDrop extends Thread{

    private final Connection sqlDB;
    private final DB mongoDB;

    @Override
    public void run() {

            dropMongo();

    }

    public void dropMongo(){
        this.mongoDB.getCollection("Sensor_Porta").drop();
        this.mongoDB.getCollection("Sensor_Temperatura").drop();
        System.out.println("Dados Apagados no Mongo");
    }



}