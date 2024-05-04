package MongoDB.mappers;

import MongoDB.entities.DadosTemperaturaMongoDB;
import MongoDB.entities.enums.DadosTemperaturaMongoFields;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class DadosTemperaturaMongoDBMapper {

    public static List<DadosTemperaturaMongoDB> mapList(Iterator<DBObject> result) {
        var resultList = new ArrayList<DadosTemperaturaMongoDB>();

        while (result.hasNext()) {
            var obj = result.next();
            var dadosTemperaturaMongoDB = new DadosTemperaturaMongoDB();

            DadosTemperaturaMongoFields.getAllFields().forEach(fieldEnum -> {
                var value = obj.get(fieldEnum.getColumn());
                fieldEnum.set(dadosTemperaturaMongoDB, value);
            });

            resultList.add(dadosTemperaturaMongoDB);
        }
        return resultList;
    }
}