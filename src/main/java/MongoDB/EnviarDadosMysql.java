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
        var processedData = queue.popData();
        System.out.println("batata");
        System.out.println(processedData);
    }
}
