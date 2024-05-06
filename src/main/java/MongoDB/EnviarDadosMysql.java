package MongoDB;

import MongoDB.entities.CurrentExperiencia;
import MongoDB.entities.DadosQueue;
import MongoDB.entities.DadosTemperaturaMongoDB;
import MongoDB.entities.enums.ExperienciaStatus;
import com.mongodb.DB;
import lombok.*;
import java.sql.Connection;
import java.util.Queue;

import lombok.Builder;

@RequiredArgsConstructor
public class EnviarDadosMysql extends Thread{
    private final Connection sqlDb;
    private DadosQueue queue = DadosQueue.getInstance();

    @Override
    public void run() {
        //retira da lista envio para mysql

//        queue.popExperiencia();
//        var treatedTeps = queue.popTempsTratadas();
//
//        var processedPortas = queue.popPortasMongo();

        // Vai estar em wait, e só vai acordar e enviar os dados quando o estado da experiencia for Terminado
        CurrentExperiencia.getInstance().getIfEstadoEquals(ExperienciaStatus.TERMINADA);
        System.out.println("INICIEI!!!");
    }
}
