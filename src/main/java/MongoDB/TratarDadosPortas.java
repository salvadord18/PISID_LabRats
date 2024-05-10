package MongoDB;

import MongoDB.entities.Corredor;
import MongoDB.entities.CurrentExperiencia;
import MongoDB.entities.DadosQueue;
import MongoDB.entities.DadosTemperaturaMongoDB;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import lombok.RequiredArgsConstructor;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class TratarDadosPortas extends Thread {

    private final Connection sqlDb;
    private final DB mongoDb;
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

            System.out.println("A sala origem é " + salaOrigem);
            System.out.println("A sala destino é " + salaDestino);

            var corredoresSize = experiencia.getExperiencia().getCorredores().length;

            for (int i = 0; i < corredoresSize; i++) {
                if (experiencia.getExperiencia().getCorredores()[i].getSalaOrigem().equals(salaOrigem)
                        && experiencia.getExperiencia().getCorredores()[i].getSalaDestino().equals(salaDestino)) {
                    queue.pushPortasTratadas(List.of(portasData));
                    System.out.println("Dados corretos inseridos nas Portas Tratadas");
                }
                String CallSP = "{ call CriarAlertaSensorDadosInvalidos(?) }";
                CallableStatement c = sqlDb.prepareCall(CallSP);
                Timestamp timestamp = Timestamp.valueOf(portasData.getHora());
                c.setTimestamp(1, timestamp);
                c.execute();
            }
// ............... Numero ratos em cada sala ...................
            String SPNumRatos = "{ call Get_RatosAtuais(?,?,?) }";
            CallableStatement c = sqlDb.prepareCall(SPNumRatos);
//            c.setInt(1, Integer.parseInt((experiencia.getExperiencia().getId())));
//            c.setInt(2, Integer.parseInt(salaDestino));
            c.setInt(1, 8);
            c.setInt(2, 2);
            c.registerOutParameter(3, Types.INTEGER);
            c.execute();

            int numeroAtualRatos = c.getInt(3);

            //Chamar SP para limiteRatosFinal
            String SPLimiteRatos = "{ call GetLimiteRatosSala(?) }";
            CallableStatement cbs = sqlDb.prepareCall(SPLimiteRatos);
            cbs.registerOutParameter(1, Types.INTEGER);
            cbs.execute();
            int limiteRatosFinal =cbs.getInt(1);

            int limiteAmarelo = (int) (limiteRatosFinal * 0.75);
            if(numeroAtualRatos <= limiteAmarelo){
                String SPRatosAtuais = "{ call CriarAlertaRatosAmarelo(?,?) }";
                CallableStatement call = sqlDb.prepareCall(SPRatosAtuais);
                call.setInt(1, Integer.parseInt((experiencia.getExperiencia().getId())));
                call.setInt(2, Integer.parseInt(salaDestino));
                call.execute();
            } else if (numeroAtualRatos >= limiteRatosFinal) {
                String SPRatosAtuais = "{ call CriarAlertaRatosVermelho(?,?) }";
                CallableStatement call = sqlDb.prepareCall(SPRatosAtuais);
                call.setInt(1, Integer.parseInt((experiencia.getExperiencia().getId())));
                call.setInt(2, Integer.parseInt(salaDestino));
                call.execute();
            }
//................... Segundos sem movimento .......................

            // por acabar
//            int segundosSemMovimento = 0;
//
//            if (segundosSemMovimento >= 60 && segundosSemMovimento < 120) {
//                //Criar alerta sem movimento amarelo
////                String procedureCall = "{CALL CriarAlertaSemMovimentosAmarelo(?,?,?,?)}";
////                CallableStatement c = sqlDb.prepareCall(procedureCall);
////                Timestamp timestamp = Timestamp.valueOf(portasData.getHora());
////                c.setTimestamp(1, timestamp);
////                c.setTimestamp(2, timestamp);
////                c.setInt(3, );
//                System.out.println("SEM MOVIMENTOS AMARELO");
//            } else if (segundosSemMovimento >= 120) {
//                //Criar alerta sem movimentos vermelho
//                System.out.println("SEM MOVIMENTOS VERMELHO");
//            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}


