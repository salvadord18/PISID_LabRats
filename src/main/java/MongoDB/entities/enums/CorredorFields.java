package MongoDB.entities.enums;

import MongoDB.entities.Corredor;

import java.util.List;

public enum CorredorFields {

    SALA_ORIGEM("Sala_Origem_ID", (e, value) -> e.setSalaOrigem((String) value)),
    SALA_DESTINO("Sala_Destino_ID", (e, value) -> e.setSalaDestino((String) value));

    private String column;
    private FieldSetter setter;

    CorredorFields(String salaOrigem, FieldSetter setter) {
        this.setter = setter;
        this.column = salaOrigem;
    }

    public static List<CorredorFields> getAll() {
        return List.of(CorredorFields.values());
    }

    public String getColumn() {
        return column;
    }

    public void set(Corredor corredor, Object value) {
        setter.setField(corredor, value);
    }

    //Formato da função lambda
    @FunctionalInterface
    private interface FieldSetter {
        void setField(Corredor c, Object value);
    }
}
