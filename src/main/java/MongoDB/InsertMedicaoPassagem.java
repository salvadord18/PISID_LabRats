package MongoDB;

import MongoDB.entities.CurrentExperiencia;
import MongoDB.entities.DadosQueue;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

@RequiredArgsConstructor
public class InsertMedicaoPassagem extends Thread {
    private final Connection sqlDb;
    private DadosQueue queue = DadosQueue.getInstance();
    private CurrentExperiencia experiencia = CurrentExperiencia.getInstance();

    @Override
    public void run() {
        try {
            insertMedicaoPassagem();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void insertMedicaoPassagem() throws SQLException {
        while (true) {

            // Vai enviar continuamente os dados

            var experienciaId = Integer.parseInt(experiencia.getExperiencia().getId());

            var treatedPassagem = queue.popPortasTratadas();
            var horaMedicao = treatedPassagem.getHora();

            Timestamp dataHora = Timestamp.valueOf(horaMedicao);

            var salaOrigem = treatedPassagem.getSalaOrigem();
            var salaDestino = treatedPassagem.getSalaDestino();

            String SPInsertPassagem = "{ call Insert_MedicaoPassagem(?,?,?,?) }";
            CallableStatement cs = sqlDb.prepareCall(SPInsertPassagem);
            cs.setInt(1, experienciaId);
            cs.setInt(2, salaOrigem);
            cs.setInt(3, salaDestino);
            cs.setTimestamp(4, dataHora);
            cs.execute();
        }
    }
}

