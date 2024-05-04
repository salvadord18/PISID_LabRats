package MongoDB.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DadosPortasMongoDB {

        private ObjectId id;
        private String hora;
        private int salaOrigem;
        private int salaDestino;

        @Override
        public String toString() {
            return "DadosPortasMongoDB{" +
                    "hora='" + hora + '\'' +
                    ", salaOrigem=" + salaOrigem +
                    ", salaDestino=" + salaDestino +
                    '}';
        }
}
