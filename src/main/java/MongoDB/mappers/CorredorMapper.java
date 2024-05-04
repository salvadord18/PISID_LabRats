package MongoDB.mappers;

import MongoDB.entities.Corredor;
import MongoDB.entities.enums.CorredorFields;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CorredorMapper {

    public static List<Corredor> mapList(ResultSet result) throws SQLException {
        var resultList = new ArrayList<Corredor>();

        while (result.next()) {
            var corredor = new Corredor();
            CorredorFields.getAll().forEach(fieldEnum -> {
                try {
                    var value = result.getString(fieldEnum.getColumn());
                    fieldEnum.set(corredor, value);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            resultList.add(corredor);
        }
        return resultList;
    }
}
