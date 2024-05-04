package MongoDB;

import MongoDB.entities.DadosQueue;
import MongoDB.entities.DadosTemperaturaMongoDB;
import MongoDB.mappers.DadosTemperaturaMongoDBMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class ProcessarTemperatura extends Thread {
    private final DB mongoDb;
    private DadosQueue queue = DadosQueue.getInstance();

    @Override
    public void run() {
        BasicDBObject query = new BasicDBObject("catch", new BasicDBObject("$exists", false));

        var collection = mongoDb.getCollection("Sensor_Temperatura");
        var iterator = collection.find(query).iterator();
        var mappedTemps = DadosTemperaturaMongoDBMapper.mapList(iterator);

        queue.pushData(mappedTemps);


        BasicDBObject idQuery = new BasicDBObject("_id", new BasicDBObject("$in", mappedTemps.stream().map(DadosTemperaturaMongoDB::getId).toList()));
        BasicDBObject update = new BasicDBObject("$set", new BasicDBObject("catch", "P"));

        collection.updateMulti(idQuery, update);

    }
}
