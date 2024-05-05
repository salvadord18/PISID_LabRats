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
import java.util.*;

@RequiredArgsConstructor
public class TratarDados extends Thread {
    private List<Integer> leituras = new ArrayList<>();
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

            // if outlier então
            if (isOutlier(tempData)) {
                outlierCount++;
                int maxNumOutliers = executeSPNumberMaxOutliers();

                BasicDBObject idQuery = new BasicDBObject("_id", tempData.getId());
                BasicDBObject update = new BasicDBObject("$set", new BasicDBObject("outlier", "O"));
                collection.update(idQuery, update);

                if (outlierCount <= maxNumOutliers) {
                    queue.pushTempsTratadas(List.of(tempData));

                    String CallSP = "{ call CriarAlertaOutlierAmarelo(?) }";
                    CallableStatement c = sqlDb.prepareCall(CallSP);
                    c.setInt(1, outlierCount);
                    c.execute();
                } else {
                    CallableStatement cs = null;
                    cs = sqlDb.prepareCall("{call CriarAlertaOutlierVermelho}");
                    cs.executeQuery();
                }
            } else {
                queue.pushTempsTratadas(List.of(tempData));
                System.out.println("Não é outlier");
            }

        }

    }

    public boolean isOutlier(DadosTemperaturaMongoDB dadosTemperaturaMongoDB) {
        GFG gfg = new GFG();

        if (leituras.size() < 40) {
            leituras.add(dadosTemperaturaMongoDB.getLeitura());
            return false;
        }
        int IQR = gfg.IQR(leituras, leituras.size());
        if(dadosTemperaturaMongoDB.getLeitura()>IQR){
            return true;
        }
        leituras.add(0, dadosTemperaturaMongoDB.getLeitura());
        leituras.remove(leituras.size() - 1);

        return false;
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


    public class GFG {

        // Function to give
        // index of the median
        public int median(List<Integer> a,
                          int l, int r) {
            int n = r - l + 1;
            n = (n + 1) / 2 - 1;
            return n + l;
        }

        // Function to
        // calculate IQR
        public int IQR(List<Integer> a, int n) {
           Collections.sort(a);

            // Index of median
            // of entire data
            int mid_index = median(a, 0, n - 1);

            // Median of first half
            int Q1;
            if (n % 2 == 0)
                Q1 = a.get(median(a, 0, mid_index));
            else
                Q1 = a.get(median(a, 0, mid_index - 1));

            // Median of second half
            int Q3;
            if (n % 2 == 0)
                Q3 = a.get(median(a, mid_index + 1, n - 1));
            else
                Q3 = a.get(median(a, mid_index + 1, n));

            // IQR calculation
            return (Q3 - Q1);
        }
    }
}
