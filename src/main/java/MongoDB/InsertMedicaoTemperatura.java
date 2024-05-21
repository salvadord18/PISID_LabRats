package MongoDB;

import MongoDB.entities.CurrentExperiencia;
import MongoDB.entities.DadosQueue;
import MongoDB.entities.enums.ExperienciaStatus;
import lombok.*;

import java.math.BigDecimal;
import java.sql.*;

@RequiredArgsConstructor
public class InsertMedicaoTemperatura extends Thread {
    private final Connection sqlDb;
    private DadosQueue queue = DadosQueue.getInstance();
    private CurrentExperiencia experiencia = CurrentExperiencia.getInstance();

    @Override
    public void run() {
        try {
            insertMedicaoTemperatura();
        } catch (SQLException e) {
            System.out.println("A exceção é " + e.getMessage());
        }

    }

    public void insertMedicaoTemperatura() throws SQLException {
        while (!CurrentExperiencia.getInstance().isEstado(ExperienciaStatus.TERMINADA)) {

            // Vai enviar continuamente os dados
//            var treatedTemp = queue.popTempsTratadas();
            var treatedTempsList = queue.getDadosTratadosTemperaturas();
            var experienciaId = Integer.parseInt(experiencia.getExperiencia().getId());

//            var leitura = treatedTemp.getLeitura();
//            var sensor = treatedTemp.getSensor();
//            var horaMedicao = treatedTemp.getHora();
//            Timestamp dataTemperatura = Timestamp.valueOf(horaMedicao);

            for (int i = 0; i < treatedTempsList.size(); i++) {
                var treatedTemp = treatedTempsList.get(i);
                var horaMedicao = treatedTemp.getHora();

                Timestamp dataTemperatura = Timestamp.valueOf(horaMedicao);
                double leitura = treatedTemp.getLeitura();
                var sensor = treatedTemp.getSensor();

                String SPInsertTemp = "{ call Insert_MedicaoTemperatura(?,?,?,?) }";
                CallableStatement cs = sqlDb.prepareCall(SPInsertTemp);
                cs.setInt(1, experienciaId);
                cs.setTimestamp(2, dataTemperatura);
                cs.setBigDecimal(3, BigDecimal.valueOf(leitura));
                cs.setInt(4, sensor);
                cs.execute();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
