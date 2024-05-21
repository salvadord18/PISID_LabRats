package SQLCloud;

import java.lang.reflect.Field;
import java.util.List;

public enum CorredorFieldsCloud {
    SALA_ORIGEM("salaa", "salaOrigem"),
    SALA_DESTINO("salab", "salaDestino");

    private final String column;
    private final String field;

    CorredorFieldsCloud(String column, String field) {
        this.column = column;
        this.field = field;
    }

    public String getColumn() {
        return column;
    }

    public void set(Corredor corredor, Object value) {
        try {
            Field field = Corredor.class.getDeclaredField(this.field);
            field.setAccessible(true);
            field.set(corredor, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static List<CorredorFieldsCloud> getAll() {
        return List.of(CorredorFieldsCloud.values());
    }
}


