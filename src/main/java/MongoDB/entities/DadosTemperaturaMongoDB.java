package MongoDB.entities;
import lombok.*;
import org.bson.types.ObjectId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DadosTemperaturaMongoDB {
    private ObjectId id;
    private String hora;
    private int leitura;
    private int sensor;


    @Override
    public String toString() {
        return "DadosTemperaturaMongoDB{" +
                "hora='" + hora + '\'' +
                ", leitura=" + leitura +
                ", sensor=" + sensor +
                '}';
    }
}
