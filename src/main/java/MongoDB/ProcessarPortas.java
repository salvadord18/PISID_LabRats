package MongoDB;

import MongoDB.entities.CurrentExperiencia;
import MongoDB.entities.DadosPortasMongoDB;
import MongoDB.entities.DadosQueue;
import MongoDB.entities.DadosTemperaturaMongoDB;
import MongoDB.entities.enums.ExperienciaStatus;
import MongoDB.mappers.DadosPortasMongoDBMapper;
import MongoDB.mappers.DadosTemperaturaMongoDBMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@RequiredArgsConstructor
public class ProcessarPortas extends Thread {
    private final DB mongoDb;
    private DadosQueue queue = DadosQueue.getInstance();

    @Override
    public void run() {
        // Vai correr enquanto o estado da experiencia estiver em execucao
        while (CurrentExperiencia.getInstance().isEstado(ExperienciaStatus.EM_CURSO)) {
            BasicDBObject query = new BasicDBObject("catch", new BasicDBObject("$exists", false));

            var collection = mongoDb.getCollection("Sensor_Porta");
            var iterator = collection.find(query).iterator();
            var mappedPortas = DadosPortasMongoDBMapper.mapList(iterator);

            queue.pushPortasMongo(mappedPortas);

            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

            LocalDateTime dataExperiencia = LocalDateTime.parse(CurrentExperiencia.getInstance().getExperiencia().getDataHora(), formatter1);
            System.out.println("data experiencia --> " + dataExperiencia);

            // só vai atualizar os dados em que a sua data seja maior que a data da experiencia
            var portasToProcess = new ArrayList<ObjectId>();
            for (int i = 0; i < mappedPortas.size(); i++) {
                var dataDadosMongo = mappedPortas.get(i).getHora();

                LocalDateTime dataTemperatura = LocalDateTime.parse(dataDadosMongo, formatter1);
                if (dataTemperatura.isAfter(dataExperiencia)) {
                    portasToProcess.add(mappedPortas.get(i).getId());
                }
            }

            BasicDBObject idQuery = new BasicDBObject("_id", new BasicDBObject("$in", portasToProcess));
            BasicDBObject update = new BasicDBObject("$set", new BasicDBObject("catch", "P"));

            collection.updateMulti(idQuery, update);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println("Processei Portas");
    }
}
