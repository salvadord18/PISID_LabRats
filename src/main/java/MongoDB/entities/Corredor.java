package MongoDB.entities;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Corredor {
    private String salaOrigem;
    private String salaDestino;

    public String getSalaDestinoComOrigem(String salaOrigem){
        return salaDestino;
    }
}
