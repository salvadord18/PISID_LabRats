package MongoDB;

import MongoDB.entities.DadosQueue;
import MongoDB.entities.DadosTemperaturaMongoDB;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import lombok.RequiredArgsConstructor;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@RequiredArgsConstructor
public class TratarDados extends Thread {
    private final Connection sqlDb;
    private final DB mongoDb;
    private int outlierCount = 0;
    private DadosQueue queue = DadosQueue.getInstance();

    @Override
    public void run() {
        try {
            tratarDadosTemperatura();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    public void tratarDadosTemperatura() throws SQLException {
        while (true) {
            var tempData = queue.popData();
            var collection = mongoDb.getCollection("Sensor_Temperatura");
            System.out.println("batata");

            // if outlier então
            if (isOutlier(tempData)) {
                outlierCount++;
                int maxNumOutliers = executeSPNumberMaxOutliers();

                BasicDBObject idQuery = new BasicDBObject("_id", tempData.getId());
                BasicDBObject update = new BasicDBObject("$set", new BasicDBObject("outlier", "O"));
                collection.update(idQuery, update);

                if (outlierCount <= maxNumOutliers) {
                    queue.pushTempsTratadas(List.of(tempData));
                }
            } else {
                queue.pushTempsTratadas(List.of(tempData));
            }

        }

    }

    public boolean isOutlier(DadosTemperaturaMongoDB dadosTemperaturaMongoDB) {
        //falta lógica do intervalo interquartilico
        return true;
    }

    public int executeSPNumberMaxOutliers() throws SQLException {
        String CallSP = "{ call GetNumOutliersMaximo(?) }";
        CallableStatement c = sqlDb.prepareCall(CallSP);
        c.registerOutParameter(1, Types.INTEGER);
        c.execute();
        int maxNumberOutliers = c.getInt(1);

        return maxNumberOutliers;

    }

    public void tratarDadosPortas() {

    }
}
