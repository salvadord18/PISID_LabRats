package MongoDB.mappers;

import MongoDB.entities.Experiencia;
import MongoDB.entities.enums.ExperienciaFields;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//Vai existir um para cada tabela do mysql
public class ExperienciaMapper {

    public static List<Experiencia> mapList(ResultSet result) throws SQLException {
        var resultList = new ArrayList<Experiencia>();

        while (result.next()) {
            var experiencia = new Experiencia();
            ExperienciaFields.getAll().forEach(fieldEnum -> {
                try {
                    var value = result.getString(fieldEnum.getColumn());
                    fieldEnum.set(experiencia, value);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            resultList.add(experiencia);
        }
        return resultList;
    }
}