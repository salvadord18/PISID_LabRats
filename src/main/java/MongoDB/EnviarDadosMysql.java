package MongoDB;

import MongoDB.entities.DadosQueue;
import MongoDB.entities.DadosTemperaturaMongoDB;
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

//        var processedTemps = queue.popData();
        var treatedTeps = queue.popTempsTratadas();

        var processedPortas = queue.popPortasMongo();

        System.out.println(treatedTeps);
        System.out.println(processedPortas);
    }
}
