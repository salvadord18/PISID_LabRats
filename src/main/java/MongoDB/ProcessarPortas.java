package MongoDB;

import MongoDB.entities.DadosPortasMongoDB;
import MongoDB.entities.DadosQueue;
import MongoDB.entities.DadosTemperaturaMongoDB;
import MongoDB.mappers.DadosPortasMongoDBMapper;
import MongoDB.mappers.DadosTemperaturaMongoDBMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProcessarPortas extends Thread{
    private final DB mongoDb;
    private DadosQueue queue = DadosQueue.getInstance();

    @Override
    public void run() {
        BasicDBObject query = new BasicDBObject("catch", new BasicDBObject("$exists", false));

        var collection = mongoDb.getCollection("Sensor_Porta");
        var iterator = collection.find(query).iterator();
        var mappedPortas = DadosPortasMongoDBMapper.mapList(iterator);

        queue.pushPortasMongo(mappedPortas);


        BasicDBObject idQuery = new BasicDBObject("_id", new BasicDBObject("$in", mappedPortas.stream().map(DadosPortasMongoDB::getId).toList()));
        BasicDBObject update = new BasicDBObject("$set", new BasicDBObject("catch", "P"));

        collection.updateMulti(idQuery, update);
        System.out.println("Success");

    }
}
