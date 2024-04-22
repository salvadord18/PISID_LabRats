package MongoDB.entities.enums;

import MongoDB.entities.Experiencia;

import java.util.List;

// Enum para cada coluna da tabela com função que faz set a cada coluna, nos seus respetivos objetos
public enum ExperienciaFields {
    DATA_HORA("DataHora", (e, value) -> e.setDataHora((String) value)),
    ID("Experiencia_ID", (e, value) -> e.setId((String) value));
    private String column;
    private FieldSetter setter;

    ExperienciaFields(String dataHora, FieldSetter setter) {
        this.column = dataHora;
        this.setter = setter;
    }

    public static List<ExperienciaFields> getAll() {
        return List.of(ExperienciaFields.values());
    }

    public String getColumn() {
        return column;
    }

    public void set(Experiencia experiencia, Object value) {
        setter.setField(experiencia, value);
    }

    //Formato da função lambda
    @FunctionalInterface
    private interface FieldSetter {
        void setField(Experiencia e, Object value);
    }
}
