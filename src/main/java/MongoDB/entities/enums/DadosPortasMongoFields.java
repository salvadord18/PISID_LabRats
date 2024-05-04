package MongoDB.entities.enums;

import MongoDB.entities.DadosPortasMongoDB;
import MongoDB.entities.DadosTemperaturaMongoDB;
import org.bson.types.ObjectId;

import java.util.List;

public enum DadosPortasMongoFields {
    HORA("Hora", (e, value) -> e.setHora((String) value)),
    ID("_id", (e, value) -> e.setId((ObjectId) value)),
    SALAORIGEM("SalaOrigem", (e, value) -> e.setSalaOrigem((int) value)),
    SALADESTINO("SalaDestino", (e, value) -> e.setSalaDestino((int) value));
    private String column;
    private FieldSetter setter;

    DadosPortasMongoFields(String column, FieldSetter setter) {
        this.column = column;
        this.setter = setter;
    }

    public static List<DadosPortasMongoFields> getAllFields() {
        return List.of(DadosPortasMongoFields.values());
    }

    public String getColumn() {
        return column;
    }

    public void set(DadosPortasMongoDB dadosPortasMongoDB, Object value) {
        setter.setField(dadosPortasMongoDB, value);
    }

    //Formato da função lambda
    @FunctionalInterface
    private interface FieldSetter {
        void setField(DadosPortasMongoDB e, Object value);
    }
}
