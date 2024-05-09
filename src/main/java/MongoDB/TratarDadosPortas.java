package MongoDB;

import MongoDB.entities.Corredor;
import MongoDB.entities.CurrentExperiencia;
import MongoDB.entities.DadosQueue;
import MongoDB.entities.DadosTemperaturaMongoDB;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import lombok.RequiredArgsConstructor;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class TratarDadosPortas extends Thread {
    private List<Integer> leituras = new ArrayList<>();
    private final Connection sqlDb;
    private final DB mongoDb;
    private int outlierCount = 0;
    private DadosQueue queue = DadosQueue.getInstance();
    private CurrentExperiencia experiencia = CurrentExperiencia.getInstance();

    @Override
    public void run() {
        try {
            tratarDadosPortas();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void tratarDadosPortas() throws SQLException {
        while (true) {
            var portasData = queue.popPortasMongo();
            var collection = mongoDb.getCollection("Sensor_Porta");
            var salaOrigem = String.valueOf(portasData.getSalaOrigem());
            var salaDestino = String.valueOf(portasData.getSalaDestino());

            System.out.println(experiencia.getExperiencia().getCorredores().length);

            var corredoresSize = experiencia.getExperiencia().getCorredores().length;

            for (int i = 0; i < corredoresSize; i++) {
                if (experiencia.getExperiencia().getCorredores()[i].getSalaOrigem().equals(salaOrigem)
                        && experiencia.getExperiencia().getCorredores()[i].getSalaDestino().equals(salaDestino)) {
                    queue.pushPortasTratadas(List.of(portasData));
                    System.out.println("Dados corretos inseridos nas Portas Tratadas");
                }
            }

                String CallSP = "{ call CriarAlertaSensorDadosInvalidos(?) }";
                CallableStatement c = sqlDb.prepareCall(CallSP);
                Timestamp timestamp = Timestamp.valueOf(portasData.getHora());
                c.setTimestamp("HoraEscrita", timestamp);
                c.execute();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


}


