package MongoDB;

import MongoDB.entities.CurrentExperiencia;
import MongoDB.entities.DadosQueue;
import MongoDB.entities.DadosTemperaturaMongoDB;
import MongoDB.entities.Experiencia;
import MongoDB.entities.enums.ExperienciaStatus;
import MongoDB.mappers.DadosTemperaturaMongoDBMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


@RequiredArgsConstructor
public class ProcessarTemperatura extends Thread {
    private final DB mongoDb;
    private DadosQueue queue = DadosQueue.getInstance();

    @Override
    public void run() {
        try{

            // Vai correr enquanto o estado da experiencia estiver em execucao
            while (CurrentExperiencia.getInstance().isEstado(ExperienciaStatus.EM_CURSO)) {

                BasicDBObject query = new BasicDBObject("catch", new BasicDBObject("$exists", false));
                var collection = mongoDb.getCollection("Sensor_Temperatura");
                var iterator = collection.find(query).iterator();
                var mappedTemps = DadosTemperaturaMongoDBMapper.mapList(iterator);

                DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

                System.out.println("Hora da current experiencia " + CurrentExperiencia.getInstance().getExperiencia().getDataHora());
                LocalDateTime dataExperiencia = LocalDateTime.parse(CurrentExperiencia.getInstance().getExperiencia().getDataHora(), formatter1);
//            System.out.println(dataExperiencia);

                // só vai atualizar os dados em que a sua data seja maior que a data da experiencia
                var tempsToProcess = new ArrayList<ObjectId>();
                for (int i = 0; i < mappedTemps.size(); i++) {
                    var temperaturaToProcess = mappedTemps.get(i);
                    var dataDadosMongo = temperaturaToProcess.getHora();

                    LocalDateTime dataTemperatura = LocalDateTime.parse(dataDadosMongo, formatter1);
//                System.out.println("data temperatura --> " + dataTemperatura);
                    if (dataTemperatura.isAfter(dataExperiencia)) {
                        tempsToProcess.add(temperaturaToProcess.getId());
                        queue.pushData(temperaturaToProcess);
                    }
                }
                BasicDBObject idQuery = new BasicDBObject("_id", new BasicDBObject("$in", tempsToProcess));
                BasicDBObject update = new BasicDBObject("$set", new BasicDBObject("catch", "P"));

                collection.updateMulti(idQuery, update);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
            System.out.println("Processei temperatura");
        }catch (Exception e){
            System.out.println("Excecao na thread ProcessarTemperatura " + e.getMessage());
        }
    }
}
