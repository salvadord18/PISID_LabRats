package MongoDB.entities.enums;

import MongoDB.entities.DadosTemperaturaMongoDB;
import org.bson.types.ObjectId;

import java.util.List;

// Enum para cada coluna da tabela com função que faz set a cada coluna, nos seus respetivos objetos
public enum DadosTemperaturaMongoFields {
    HORA("Hora", (e, value) -> e.setHora((String) value)),
    ID("_id", (e, value) -> e.setId((ObjectId) value)),
    LEITURA("Leitura", (e, value) -> e.setLeitura((int) value)),
    SENSOR("Sensor", (e, value) -> e.setSensor((int) value));
    private String column;
    private FieldSetter setter;

    DadosTemperaturaMongoFields(String column, FieldSetter setter) {
        this.column = column;
        this.setter = setter;
    }

    public static List<DadosTemperaturaMongoFields> getAllFields() {
        return List.of(DadosTemperaturaMongoFields.values());
    }

    public String getColumn() {
        return column;
    }

    public void set(DadosTemperaturaMongoDB dadosTemperaturaMongoDB, Object value) {
        setter.setField(dadosTemperaturaMongoDB, value);
    }

    //Formato da função lambda
    @FunctionalInterface
    private interface FieldSetter {
        void setField(DadosTemperaturaMongoDB e, Object value);
    }
}
