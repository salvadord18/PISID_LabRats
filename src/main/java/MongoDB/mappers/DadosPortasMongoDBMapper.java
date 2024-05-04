package MongoDB.mappers;

import MongoDB.entities.DadosPortasMongoDB;
import MongoDB.entities.enums.DadosPortasMongoFields;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DadosPortasMongoDBMapper {

    public static List<DadosPortasMongoDB> mapList(Iterator<DBObject> result) {
        var resultList = new ArrayList<DadosPortasMongoDB>();

        while (result.hasNext()) {
            var obj = result.next();
            var dadosPortasMongoDB = new DadosPortasMongoDB();

            DadosPortasMongoFields.getAllFields().forEach(fieldEnum -> {
                var value = obj.get(fieldEnum.getColumn());
                fieldEnum.set(dadosPortasMongoDB, value);
            });

            resultList.add(dadosPortasMongoDB);
        }
        return resultList;
    }
}
