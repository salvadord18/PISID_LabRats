package MongoDB;

import MongoDB.entities.CurrentExperiencia;
import MongoDB.entities.DadosQueue;
import MongoDB.entities.Experiencia;
import MongoDB.entities.enums.ExperienciaStatus;
import lombok.RequiredArgsConstructor;

import java.sql.*;
import java.time.LocalDateTime;

//Thread que vai verificar o estado da experiencia e atualiza-lo
// Por fazer, o código ainda é apenas para teste
@RequiredArgsConstructor
public class CheckExperienciaStatus extends Thread {
    private final Connection sqlDb;
    private DadosQueue queue = DadosQueue.getInstance();
    private CurrentExperiencia experiencia = CurrentExperiencia.getInstance();

    @Override
    public void run() {
        try {
            validaIfExperienciaTerminou();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void validaIfExperienciaTerminou() throws SQLException, InterruptedException {
        int estadoExperiencia = 0;
        while (true) {
            System.out.println("Ainda não executou o Get_EstadoExperiencia");
            String testeCallSP = "{CALL Get_EstadoExperiencia (?,?)}";
            CallableStatement cs = sqlDb.prepareCall(testeCallSP);
            cs.setInt(1, Integer.valueOf(experiencia.getExperiencia().getId()));
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();

            estadoExperiencia = cs.getInt(2);
            System.out.println("O estado da experiencia é " + estadoExperiencia);

            LocalDateTime currentTime = LocalDateTime.now();
            Timestamp dataHora = Timestamp.valueOf(currentTime);

            if (estadoExperiencia == 5 || estadoExperiencia == 7) {
                System.out.println("Experiencia " + Integer.valueOf(experiencia.getExperiencia().getId()) + " Terminada.");
                CurrentExperiencia.getInstance().setEstadoExperiencia(ExperienciaStatus.TERMINADA);

                String fimExperiencia = "{CALL CriarAlertaFimExperiencia (?,?)}";
                CallableStatement callableStatement = sqlDb.prepareCall(fimExperiencia);
                callableStatement.setInt(1, Integer.valueOf(experiencia.getExperiencia().getId()));
                callableStatement.setTimestamp(2, dataHora);
                callableStatement.execute();

                for(Thread thread : MainMongoDB.threadsList){
                    System.out.println("threads interrompidas");
                    thread.interrupt();
                }
                break;
            }
            Thread.sleep(1000);
        }
    }
}
