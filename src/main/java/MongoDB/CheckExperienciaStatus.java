package MongoDB;

import MongoDB.entities.CurrentExperiencia;
import MongoDB.entities.DadosQueue;
import MongoDB.entities.Experiencia;
import MongoDB.entities.enums.ExperienciaStatus;
import lombok.RequiredArgsConstructor;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

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
            String testeCallSP = "{CALL Get_EstadoExperiencia (?,?)}";
            CallableStatement cs = sqlDb.prepareCall(testeCallSP);
            cs.setInt(1, Integer.valueOf(experiencia.getExperiencia().getId()));
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();

            estadoExperiencia = cs.getInt(2);

            if (estadoExperiencia == 5) {
                System.out.println("Experiencia " + Integer.valueOf(experiencia.getExperiencia().getId()) + " Terminada.");
                CurrentExperiencia.getInstance().setEstadoExperiencia(ExperienciaStatus.TERMINADA);
                for(Thread thread : MainMongoDB.threadsList){
                    System.out.println("threads interrompidas");
                    thread.interrupt();
                }
                break;
            }
            Thread.sleep(2000);
        }
    }
}
