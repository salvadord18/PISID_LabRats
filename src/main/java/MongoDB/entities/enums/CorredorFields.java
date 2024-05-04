package MongoDB.entities.enums;

import MongoDB.entities.Corredor;

import java.util.List;

public class CorredorFields {


    SALA_ORIGEM("Sala_Origem_ID", (e, value) -> e.setSalaOrigem((String) value)),
    SALA_DESTINO("Sala_Destino_ID", (e, value) -> e.setSalaDestino((String) value));

    private String column;
    private CorredorFields.FieldSetter setter;



    CorredorFields(String salaOrigem, CorredorFields.FieldSetter setter) {
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
